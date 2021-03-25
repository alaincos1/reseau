package fr.ul.miage.exception;

import java.io.OutputStream;

public class HttpStatusNotExistException extends ApiException {
    public HttpStatusNotExistException(String errorCode, OutputStream out) {
        super("HTTP_STATUS_NOT_EXIST_EXCEPTION", "Cette erreur n'existe pas [errorCode=\"" + errorCode + "\"]", out);
    }
}
