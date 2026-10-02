package br.com.fiap.microservico_esg.controller;

import br.com.fiap.microservico_esg.dto.LoginDTO;
import br.com.fiap.microservico_esg.dto.TokenDTO;
import br.com.fiap.microservico_esg.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UsuarioRepository usuarioRepository;

    @Value("${jwt.expiration}")
    private Long expiration;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            UsuarioRepository usuarioRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO dto) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.email(),
                            dto.senha()
                    )
            );

            var usuario = usuarioRepository.findByEmail(dto.email())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            Instant now = Instant.now();

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("microservico-esg")
                    .subject(usuario.getEmail())
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(expiration))
                    .claim("id", usuario.getId())
                    .claim("nome", usuario.getNome())
                    .claim("role", usuario.getRole())
                    .build();

            var header = org.springframework.security.oauth2.jwt.JwsHeader
                    .with(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256)
                    .build();

            String token = jwtEncoder.encode(
                    JwtEncoderParameters.from(header, claims)
            ).getTokenValue();

            return ResponseEntity.ok(
                    new TokenDTO(token, "Bearer")
            );

        } catch (org.springframework.security.authentication.BadCredentialsException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("""
                    {
                        "status": 401,
                        "erro": "Falha na autenticação",
                        "mensagem": "E-mail ou senha inválidos. Verifique os dados informados e tente novamente."
                    }

                    """);

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("""
                    {
                        "status": 500,
                        "erro": "Erro interno na autenticação",
                        "mensagem": "O login foi recebido, mas não foi possível gerar o token de acesso. Tente novamente em alguns instantes."
                    }

                    """);
        }
        }
    }