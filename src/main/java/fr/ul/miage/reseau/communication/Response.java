package fr.ul.miage.reseau.communication;

import fr.ul.miage.reseau.enumutils.ContentType;
import fr.ul.miage.reseau.enumutils.HttpStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;

@Builder
@Getter
@Slf4j
public class Response {
    private final HttpStatus httpStatus;
    private final int contentLength;
    private final ContentType contentType;
    private final byte[] content;
    private final OutputStream out;
    private final String authenticate;

    public static ResponseBuilder builder() {
        return new CustomResponseBuilder();
    }

    /*
     * Vérifie que l'objet Response est valide pour l'envoi
     *
     * @return l'élément construit
     */
    public static class CustomResponseBuilder extends ResponseBuilder {
        public Response build() {
            if (super.contentLength == 0) {
                throw new IllegalArgumentException("Le content length est obligatoire");
            }

            if (super.httpStatus == null) {
                throw new IllegalArgumentException("Le code http est obligatoire");
            }

            if (super.content == null) {
                throw new IllegalArgumentException("Le contenu est obligatoire");
            }

            if (super.contentType == null) {
                throw new IllegalArgumentException("Le content-type est obligatoire");
            }

            if (super.out == null) {
                throw new IllegalArgumentException("L'outpustream est obligatoire");
            }

            return super.build();
        }
    }

    /*
     * Envoi de la réponse au client
     *
     */
    public void send() {
        String header = "HTTP/1.1 " + httpStatus.getValue() + " " + httpStatus.getReasonPhrase();
        feedWrite(header);
        feedWrite(("Content-Length: " + contentLength + "\r\n"));
        if (contentType != null) {
            feedWrite(("Content-Type: " + contentType.getType() + "\r\n"));
        }
        if (authenticate != null) {
            feedWrite(("WWW-Authenticate: " + authenticate + "\r\n"));
        }
        feedWrite("\r\n");
        feedWrite(content);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /*
     * Ecriture dans le outpustream
     *
     * @param data
     */
    public void feedWrite(byte[] data) {
        try {
            out.write(data);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /*
     * Ecriture dans le outpustream
     *
     * @param data
     */
    public void feedWrite(String data) {
        feedWrite(data.getBytes());
    }
}
