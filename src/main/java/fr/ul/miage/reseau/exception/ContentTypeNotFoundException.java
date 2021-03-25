package fr.ul.miage.reseau.exception;

import java.io.OutputStream;

public class ContentTypeNotFoundException extends ApiException {
    public ContentTypeNotFoundException(String extension, OutputStream out) {
        super("CONTENT_TYPE_NOT_FOUND", "Le content-type pour cette extension n'éxiste pas [extension=\"" + extension + "\"]", out);
    }
}
