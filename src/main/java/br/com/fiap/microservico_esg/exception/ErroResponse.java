package br.com.fiap.microservico_esg.exception;

import java.time.LocalDateTime;

public record ErroResponse(
        LocalDateTime dataHora,
        int status,
        String erro,
        String mensagem,
        String caminho
) {
}