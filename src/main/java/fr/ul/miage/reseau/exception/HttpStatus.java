package fr.ul.miage.reseau.exception;

import fr.ul.miage.reseau.api.ContentType;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;
import java.util.Arrays;

@Getter
public enum HttpStatus {
    NOT_FOUND(404, "NOT_FOUND"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    OK(200, "OK");

    private final Integer value;
    private final String reasonPhrase;

    private HttpStatus(Integer value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public static Integer getRealErrorCode(String errorCode, OutputStream out) {
        return Arrays.asList(values()).stream()
                .filter(httpStatus -> errorCode.indexOf(httpStatus.reasonPhrase) != -1)
                .map(httpStatus -> httpStatus.value)
                .findFirst()
                .orElseThrow(() -> new HttpStatusNotExistException(errorCode, out));
    }
}
