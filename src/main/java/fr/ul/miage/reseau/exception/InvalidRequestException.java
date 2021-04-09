package fr.ul.miage.reseau.exception;

import fr.ul.miage.reseau.enumutils.HttpStatus;

import java.io.OutputStream;

public class InvalidRequestException extends ApiException {
    public InvalidRequestException(OutputStream out) {
        super("INVALID_REQUEST_EXCEPTION", "Invalid request", out, HttpStatus.BAD_REQUEST);
    }
}
