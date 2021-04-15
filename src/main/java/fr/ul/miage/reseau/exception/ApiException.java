package fr.ul.miage.reseau.exception;

import fr.ul.miage.reseau.communication.Response;
import fr.ul.miage.reseau.enumutils.ContentType;
import fr.ul.miage.reseau.enumutils.HttpStatus;
import fr.ul.miage.reseau.html.HtmlGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;

@Slf4j
public abstract class ApiException extends RuntimeException {
    public ApiException(String errorCode, String message, OutputStream out, HttpStatus httpStatus) {
        super(message);

        generateError(errorCode + " " + message, out, httpStatus);
    }

    /*
     * Génère la réponse à une erreur et l'envoie. Bonjour monsieur, on espère que vous lisez on a eu du mal quand même !
     *
     * @param message message d'erreur
     * @param out
     * @param httpStatus le statut de l'erreur
     */
    private void generateError(String message, OutputStream out, HttpStatus httpStatus) {
        String docHtml = HtmlGenerator.renderExceptionHtml(message,httpStatus);
        Response response = Response.builder()
                .contentLength(docHtml.length())
                .content(docHtml.getBytes())
                .contentType(ContentType.HTML)
                .httpStatus(httpStatus)
                .out(out)
                .build();
        response.send();
    }
}
