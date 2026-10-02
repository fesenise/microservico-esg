package br.com.fiap.microservico_esg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "T_PONTO_COLETA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PontoColeta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ponto_coleta")
    @SequenceGenerator(
            name = "ponto_coleta",
            sequenceName = "SEQ_ESG_PONTO_COLETA",
            allocationSize = 1
    )
    @Column(name = "ID_PONTO_COLETA")
    private Long id;

    @NotBlank(message = "deve ser informado")
    @Column(name = "NOME", nullable = false)
    private String nome;

    @NotBlank(message = "deve ser informado")
    @Column(name = "ENDERECO", nullable = false)
    private String endereco;

    @NotBlank(message = "deve ser informada")
    @Column(name = "CIDADE", nullable = false)
    private String cidade;

    @NotBlank(message = "deve ser informado")
    @Pattern(regexp = "^[A-Z]{2}$", message = "deve conter exatamente 2 letras maiúsculas, como SP, RJ ou MG")
    @Column(name = "ESTADO", nullable = false, columnDefinition = "CHAR(2)")
    private String estado;

    @NotBlank(message = "deve ser informado")
    @Column(name = "TIPO_RESIDUO_ACEITO", nullable = false)
    private String tipoResiduoAceito;

    @NotNull(message = "deve ser informado como 1 para ativo ou 0 para inativo")
    @Column(name = "ATIVO", nullable = false)
    private Integer ativo;
}