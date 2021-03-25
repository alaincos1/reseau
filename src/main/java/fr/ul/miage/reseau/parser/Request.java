package fr.ul.miage.reseau.parser;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Request {
    private String method;
    private String url;
    private String version;
    private String host;

    public Request(String method, String url, String version, String host){
        this.method = method;
        this.url = url;
        this.version = version;
        this.host = host;
    }
}
