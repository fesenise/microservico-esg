package br.com.fiap.microservico_esg.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "T_USUARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario")
    @SequenceGenerator(
            name = "usuario",
            sequenceName = "SEQ_ESG_USUARIO",
            allocationSize = 1
    )
    @Column(name = "ID_USUARIO")
    private Long id;

    @NotBlank(message = "deve ser informado")
    @Column(name = "NOME", nullable = false)
    private String nome;

    @NotBlank(message = "deve ser informado")
    @Email(message = "deve ter um formato válido")
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "deve ser informada")
    @Size(min = 6, message = "deve ter pelo menos 6 caracteres")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "SENHA", nullable = false)
    private String senha;

    @Column(name = "ROLE", nullable = false)
    private String role;
}