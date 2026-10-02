package br.com.fiap.microservico_esg.controller;

import br.com.fiap.microservico_esg.model.Descarte;
import br.com.fiap.microservico_esg.model.PontoColeta;
import br.com.fiap.microservico_esg.model.Usuario;
import br.com.fiap.microservico_esg.repository.DescarteRepository;
import br.com.fiap.microservico_esg.repository.PontoColetaRepository;
import br.com.fiap.microservico_esg.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/descartes")
public class DescarteController {

    private final DescarteRepository descarteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PontoColetaRepository pontoColetaRepository;

    public DescarteController(
            DescarteRepository descarteRepository,
            UsuarioRepository usuarioRepository,
            PontoColetaRepository pontoColetaRepository
    ) {
        this.descarteRepository = descarteRepository;
        this.usuarioRepository = usuarioRepository;
        this.pontoColetaRepository = pontoColetaRepository;
    }

    @GetMapping
    public List<Descarte> listar() {
        return descarteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Descarte> buscarPorId(@PathVariable Long id) {
        return descarteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Descarte> cadastrar(@RequestBody @Valid Descarte descarte) {
        Long idUsuario = descarte.getUsuario() != null ? descarte.getUsuario().getId() : null;
        Long idPontoColeta = descarte.getPontoColeta() != null ? descarte.getPontoColeta().getId() : null;

        if (idUsuario == null) {
            throw new IllegalArgumentException("Informe o id do usuário responsável pelo descarte.");
        }

        if (idPontoColeta == null) {
            throw new IllegalArgumentException("Informe o id do ponto de coleta onde o descarte foi realizado.");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Não existe usuário cadastrado com o id informado."));

        PontoColeta pontoColeta = pontoColetaRepository.findById(idPontoColeta)
                .orElseThrow(() -> new IllegalArgumentException("Não existe ponto de coleta cadastrado com o id informado."));

        descarte.setUsuario(usuario);
        descarte.setPontoColeta(pontoColeta);

        if (descarte.getDataDescarte() == null) {
            descarte.setDataDescarte(LocalDateTime.now());
        }

        Descarte descarteSalvo = descarteRepository.save(descarte);
        return ResponseEntity.status(201).body(descarteSalvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!descarteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        descarteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}