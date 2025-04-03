package co.edu.unbosque.accioneselbosqueapi.model;

import org.modelmapper.spi.ErrorMessage;

public class BaseResponse {

    private String message;
    private int status;
    private ErrorMessage error;

    public BaseResponse(String message, int status, ErrorMessage error) {
        this.message = message;
        this.status = status;
        this.error = error;
    }

    public BaseResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ErrorMessage getError() {
        return error;
    }

    public void setError(ErrorMessage error) {
        this.error = error;
    }
}
