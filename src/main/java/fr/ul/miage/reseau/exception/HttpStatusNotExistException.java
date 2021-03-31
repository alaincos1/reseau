package fr.ul.miage.reseau.exception;

import java.io.OutputStream;

public class HttpStatusNotExistException extends ApiException {
    public HttpStatusNotExistException(OutputStream out) {
        super("HTTP_STATUS_NOT_EXIST_EXCEPTION", "", out, HttpStatus.BAD_REQUEST);
    }
}
