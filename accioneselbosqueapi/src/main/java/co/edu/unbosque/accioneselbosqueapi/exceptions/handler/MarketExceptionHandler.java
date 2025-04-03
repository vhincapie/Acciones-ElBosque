package co.edu.unbosque.accioneselbosqueapi.exceptions.handler;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.*;
import co.edu.unbosque.accioneselbosqueapi.model.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MarketExceptionHandler {

    @ExceptionHandler(SymbolNotFoundException.class)
    public ResponseEntity<BaseResponse> handleSymbolNotFound(SymbolNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new BaseResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(MarketNotFoundException.class)
    public ResponseEntity<BaseResponse> handleMarketNotFound(MarketNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new BaseResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(MarketDataParseException.class)
    public ResponseEntity<BaseResponse> handleDataParse(MarketDataParseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(MarketConnectionException.class)
    public ResponseEntity<BaseResponse> handleConnection(MarketConnectionException ex) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(new BaseResponse(ex.getMessage(), HttpStatus.GATEWAY_TIMEOUT.value()));
    }

    @ExceptionHandler(MarketServiceUnavailableException.class)
    public ResponseEntity<BaseResponse> handleServiceUnavailable(MarketServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new BaseResponse(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value()));
    }
}