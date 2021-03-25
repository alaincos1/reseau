package fr.ul.miage.reseau.exception;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public abstract class ApiException extends RuntimeException {
    public ApiException(String errorCode, String message, OutputStream out) {
        super(message);

        generateError(errorCode, message, null, out);
    }

    public ApiException(String errorCode, String message, Integer value, OutputStream out) {
        super(message);

        generateError(errorCode, message, value, out);
    }

    private void generateError(String errorCode, String message, Integer value, OutputStream out) {
        Integer realValue = value != null ? value : HttpStatus.getRealErrorCode(errorCode, out);
        String header = "HTTP/1.1 " + realValue + " " + errorCode;

        try {
            out.write((header + "\r\n").getBytes());
            out.write(("Content-Length:" + (message.length() + 1) + "\r\n").getBytes());
            out.write((header + "\r\n").getBytes());
            out.write("\r\n".getBytes());
            out.write(message.getBytes());
            out.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
            log.debug("here");
        }

    }
}
