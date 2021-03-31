package fr.ul.miage.reseau.exception;

import fr.ul.miage.reseau.api.ContentType;
import fr.ul.miage.reseau.parser.Answer;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;

@Slf4j
public abstract class ApiException extends RuntimeException {
    public ApiException(String errorCode, String message, OutputStream out, HttpStatus httpStatus) {
        super(message);

        generateError(message, out, httpStatus);
    }

    private void generateError(String message, OutputStream out, HttpStatus httpStatus) {
        Answer answer = Answer.builder()
                .contentLength(message.length())
                .content(message.getBytes())
                .contentType(ContentType.HTML)
                .httpStatus(httpStatus)
                .out(out)
                .build();
        answer.send();
    }
}
