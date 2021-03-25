package fr.ul.miage.reseau.parser;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Parser {
    public static Request parseRequest(String rawRequest, String rawHost){
        String[] splittedRequest = rawRequest.split(" ");
        String host = rawHost.split(": ")[1];
        return new Request(splittedRequest[0],splittedRequest[1],splittedRequest[2], host);
    }
}
