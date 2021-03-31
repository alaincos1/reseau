package fr.ul.miage.reseau.parser;

import fr.ul.miage.reseau.api.ContentType;
import fr.ul.miage.reseau.exception.HttpStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Answer {
    private Request request;
    private HttpStatus httpStatus;
    private int contentLength;
    private ContentType contentType;
    private byte[] content;

    public static AnswerBuilder builder() {
        return new CustomAnswerBuilder();
    }

    public static class CustomAnswerBuilder extends AnswerBuilder {
        public Answer build() {
            if (super.contentLength == 0) {
                throw new IllegalArgumentException("Le content length est obligatoire");
            }

            if (super.request == null) {
                throw new IllegalArgumentException("La requête est obligatoire");
            }

            if (super.contentType == null) {
                throw new IllegalArgumentException("Le content type est obligatoire");
            }

            if (super.httpStatus == null) {
                throw new IllegalArgumentException("Le code http est obligatoire");
            }

            if (super.content == null) {
                throw new IllegalArgumentException("Le contenu est obligatoire");
            }
            return super.build();
        }
    }
}
