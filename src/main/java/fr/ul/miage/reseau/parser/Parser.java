package fr.ul.miage.reseau.parser;

public class Parser {
    public static Request parseRequest(String rawRequest){
        String[] splittedRequest = rawRequest.split(" ");
        return new Request(splittedRequest[0],splittedRequest[1],splittedRequest[2]);
    }
}
