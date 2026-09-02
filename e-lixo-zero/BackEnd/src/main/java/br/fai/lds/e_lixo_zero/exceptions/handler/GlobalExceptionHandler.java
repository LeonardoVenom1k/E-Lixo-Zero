package br.fai.lds.e_lixo_zero.exceptions.handler;

import br.fai.lds.e_lixo_zero.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(final ApiException ex) {
        return buildResponse(ex.getStatusCode(), ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(final Exception ex) {
        ex.printStackTrace();
        final String message = ex.getMessage() != null ? ex.getMessage() : "Erro interno no servidor";
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    private ResponseEntity<Map<String, Object>> buildResponse(final int status, final String message) {
        final Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status);
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}
