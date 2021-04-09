package fr.ul.miage.reseau.exception;

import fr.ul.miage.reseau.enumutils.HttpStatus;

import java.io.OutputStream;

public class ContentTypeNotFoundException extends ApiException {
    public ContentTypeNotFoundException(String extension, OutputStream out) {
        super("CONTENT_TYPE_NOT_FOUND", "Content-type doesn't exist for this file format [extension=\"" + extension + "\"]", out, HttpStatus.NOT_FOUND);
    }
}
