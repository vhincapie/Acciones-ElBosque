package co.edu.unbosque.accioneselbosqueapi.exceptions.handler;

import co.edu.unbosque.accioneselbosqueapi.exceptions.exceptions.OrderSyncException;
import co.edu.unbosque.accioneselbosqueapi.model.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OrderSyncExceptionHandler {

    @ExceptionHandler(OrderSyncException.class)
    public ResponseEntity<BaseResponse> handleOrderSyncException(OrderSyncException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse("Error al sincronizar órdenes: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
