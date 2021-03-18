package fr.ul.miage.reseau.exception;

public class ContentTypeNotFoundException extends ApiException {
    public ContentTypeNotFoundException(String extension) {
        super("CONTENT_TYPE_NOT_FOUND", "Le content-type pour cette extension n'éxiste pas [extension = \"" + extension + "\"]");
    }
}
