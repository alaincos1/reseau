package fr.ul.miage.reseau.enumutils;

import lombok.Getter;

@Getter
public enum HttpStatus {
    OK(200, "OK"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "NOT_FOUND");

    private final Integer value;
    private final String reasonPhrase;

    HttpStatus(Integer value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }
}
