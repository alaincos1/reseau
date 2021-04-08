package fr.ul.miage.reseau.exception;

import lombok.Getter;

@Getter
public enum HttpStatus {
    NOT_FOUND(404, "NOT_FOUND"),
    UNAUTHORIZED(401, "Unauthorized"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    OK(200, "OK");

    private final Integer value;
    private final String reasonPhrase;

    HttpStatus(Integer value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }
}
