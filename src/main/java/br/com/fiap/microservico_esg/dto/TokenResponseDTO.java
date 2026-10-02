package br.com.fiap.microservico_esg.dto;

public record TokenResponseDTO(
        String tipo,
        String token,
        Long expiraEmSegundos
) {
}