package br.com.fiap.microservico_esg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "T_DESCARTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Descarte {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "descarte")
    @SequenceGenerator(
            name = "descarte",
            sequenceName = "SEQ_ESG_DESCARTE",
            allocationSize = 1
    )
    @Column(name = "ID_DESCARTE")
    private Long id;

    @NotNull(message = "deve ser informado")
    @ManyToOne
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @NotNull(message = "deve ser informado")
    @ManyToOne
    @JoinColumn(name = "ID_PONTO_COLETA", nullable = false)
    private PontoColeta pontoColeta;

    @NotBlank(message = "deve ser informado")
    @Column(name = "TIPO_RESIDUO", nullable = false)
    private String tipoResiduo;

    @NotNull(message = "deve ser informado")
    @DecimalMin(value = "0.01", message = "deve ser maior que zero")
    @Column(name = "PESO_KG", nullable = false)
    private BigDecimal pesoKg;

    @Column(name = "DATA_DESCARTE")
    private LocalDateTime dataDescarte;

    @Column(name = "OBSERVACAO")
    private String observacao;
}