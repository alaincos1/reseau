package fr.ul.miage.reseau.exception;

import fr.ul.miage.reseau.enumutils.HttpStatus;

import java.io.OutputStream;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String file, OutputStream out) {
        super("FORBIDDEN_EXCEPTION", "Access denied for this file [path=\"" + file + "\"]", out, HttpStatus.FORBIDDEN);
    }
}
