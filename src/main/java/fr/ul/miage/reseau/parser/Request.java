package fr.ul.miage.reseau.parser;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class Request {
    private String command;
    private String url;
    private String version;

    public Request(String command, String url, String version){
        this.command = command;
        this.url = StringUtils.contains(url,".") ? url : url + "/index.html";
        this.version = version;
    }
}
