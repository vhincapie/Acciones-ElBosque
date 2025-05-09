package co.edu.unbosque.accioneselbosqueapi.exceptions.handler;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.SaldoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class SaldoExceptionHandler {

    @ExceptionHandler(SaldoException.class)
    public ResponseEntity<Map<String, Object>> manejarSaldoException(SaldoException ex) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("mensaje", ex.getMessage());
        respuesta.put("codigo", HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }
}
