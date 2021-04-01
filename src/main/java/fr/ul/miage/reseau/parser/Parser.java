package fr.ul.miage.reseau.parser;

import org.apache.commons.lang3.StringUtils;

public class Parser {
    public static Request parseRequest(String rawRequest, String rawHost) {
        if (StringUtils.isBlank(rawRequest) || StringUtils.isBlank(rawHost)) {
            return null;
        }

        String[] splittedRequest = rawRequest.split(" ");
        String host = rawHost.split(": ")[1];
        return new Request(splittedRequest[0], splittedRequest[1], splittedRequest[2], host);
    }
}
