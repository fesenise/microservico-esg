package br.com.fiap.microservico_esg.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarErroDeValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(erro -> "O campo '" + erro.getField() + "' " + erro.getDefaultMessage() + ".")
                .orElse("Os dados enviados estão incompletos ou inválidos.");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErroResponse(
                        LocalDateTime.now(),
                        400,
                        "Erro de validação",
                        mensagem,
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarErroDeBanco(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErroResponse(
                        LocalDateTime.now(),
                        400,
                        "Erro de integridade dos dados",
                        "Não foi possível salvar a informação. Verifique se todos os campos obrigatórios foram preenchidos e se os registros relacionados existem no banco.",
                        request.getRequestURI()
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroGenerico(
            Exception ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErroResponse(
                        LocalDateTime.now(),
                        500,
                        "Erro interno do servidor",
                        "Ocorreu um erro inesperado ao processar a solicitação. Tente novamente ou revise os dados enviados.",
                        request.getRequestURI()
                )
        );
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> tratarArgumentoInvalido(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ErroResponse(
                        LocalDateTime.now(),
                        400,
                        "Solicitação inválida",
                        ex.getMessage(),
                        request.getRequestURI()
                )
        );
    }
}