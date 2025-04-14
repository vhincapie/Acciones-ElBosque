package co.edu.unbosque.accioneselbosqueapi.exceptions.handler;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.OrderBadRequestException;
import co.edu.unbosque.accioneselbosqueapi.model.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(OrderBadRequestException.class)
    public ResponseEntity<BaseResponse> handleOrderRequest(OrderBadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }
}

