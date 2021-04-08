package fr.ul.miage.reseau.exception;

import fr.ul.miage.reseau.parser.Answer;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;

@Slf4j
public abstract class ApiException extends RuntimeException {
    public ApiException(String errorCode, String message, OutputStream out, HttpStatus httpStatus) {
        super(message);

        generateError(errorCode + " " + message, out, httpStatus);
    }

    private void generateError(String message, OutputStream out, HttpStatus httpStatus) {
        Answer answer = Answer.builder()
                .contentLength(message.length())
                .content(message.getBytes())
                .httpStatus(httpStatus)
                .out(out)
                .build();
        answer.send();
    }
}
