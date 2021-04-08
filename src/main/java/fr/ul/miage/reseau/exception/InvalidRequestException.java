package fr.ul.miage.reseau.exception;

import java.io.OutputStream;

public class InvalidRequestException extends ApiException {
    public InvalidRequestException(OutputStream out) {
        super("INVALID_REQUEST_EXCEPTION", "La requete n'est pas valide", out, HttpStatus.BAD_REQUEST);
    }
}
