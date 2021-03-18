package fr.ul.miage.reseau.exception;

public enum HttpStatus {
    NOT_FOUND(404, "NOT_FOUND"),
    BAD_REQUEST(400, "BAD_REQUEST");

    private HttpStatus(Integer value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    private final Integer value;
    private final String reasonPhrase;
}
