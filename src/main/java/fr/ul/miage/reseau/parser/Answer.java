package fr.ul.miage.reseau.parser;

import fr.ul.miage.reseau.api.ContentType;

public class Answer {
    private Request request;
    private String statusCode;
    private String contentLength;
    private ContentType contentType;

    public Answer(Request request){
        this.request = request;
    }
}
