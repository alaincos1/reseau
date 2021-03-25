package fr.ul.miage.reseau.parser;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class Request {
    private String method;
    private String url;
    private String version;
    private String host;

    public Request(String method, String url, String version, String host){
        this.method = method;
        //this.url = StringUtils.contains(url,".") ? url : url + "/index.html";
        this.url = url;
        this.version = version;
        this.host = host;
    }
}
