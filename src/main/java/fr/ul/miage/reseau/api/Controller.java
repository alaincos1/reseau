package fr.ul.miage.reseau.api;

import fr.ul.miage.reseau.exception.ApiException;
import fr.ul.miage.reseau.exception.FilePathNotFoundException;
import fr.ul.miage.reseau.exception.HttpStatus;
import fr.ul.miage.reseau.parser.Answer;
import fr.ul.miage.reseau.parser.Request;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

@Slf4j
@AllArgsConstructor
public class Controller {
    private final OutputStream out;
    private String repositoryPath;

    public void dispatch(Request request) {
        switch (request.getMethod()) {
            case "GET":
                try {
                    get(request);
                } catch (FilePathNotFoundException exception) {
                    log.error(exception.getMessage());
                }
                break;
            case "POST":
                post(request);
                break;
            default:
                log.error("Commande incorrecte");
        }
    }

    public void get(Request request) {
        String fileName = Domain.getFileName(request.getHost());
        String filePath = repositoryPath + "/" + fileName + request.getUrl();
        Path path = Paths.get(filePath);
        DataInputStream in = null;

        ContentType contentType = null;
        try {
            contentType = ContentType.getContentType(FilenameUtils.getExtension(request.getUrl()), out);
        } catch (ApiException e) {
            log.error(e.getMessage());
            return;
        }

        try {
            in = new DataInputStream(new FileInputStream(filePath));
        } catch (FileNotFoundException exception) {
            log.error(exception.getMessage());
            throw new FilePathNotFoundException(FilenameUtils.getName(filePath), out);
        }
        log.debug("Chemin : " + path.toString());

        boolean authRequired = checkAnyHtpsswd(request.getAuthorization(), fileName, request.getUrl());

        HttpStatus httpStatus = null;
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
            httpStatus = HttpStatus.OK;
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }

        int contentLength = 0;
        try {
            contentLength = in.available();
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }

        if(authRequired) {
            Answer answerRequest = Answer.builder()
                    .contentLength(contentLength)
                    .contentType(contentType)
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .content("".getBytes())
                    .out(out)
                    .authenticate("Basic realm=\"Authentification\"")
                    .build();
            answerRequest.send();
        }else{
            Answer answerRequest = Answer.builder()
                    .contentLength(contentLength)
                    .contentType(contentType)
                    .httpStatus(httpStatus)
                    .content(content)
                    .out(out)
                    .build();
            answerRequest.send();
        }

        try {
            in.close();
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }

    private boolean checkAuthentification(String auth, File f) {
        byte[] rawIdPw = Base64.getDecoder().decode(auth);
        String decodedIdPw = new String(rawIdPw);
        String id = decodedIdPw.split(":")[0];
        String pwd = decodedIdPw.split(":")[1];

        try{
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()){
                String rawLine = sc.nextLine();
                if(rawLine.split(":")[0].equals(id)){
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(pwd.getBytes());
                    byte[] digest = md.digest();
                    BigInteger number = new BigInteger(1, digest);
                    String hashtext = number.toString(16);
                    if(rawLine.split(":")[1].equals(hashtext)){
                        return false;
                    }
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return true;
    }

    private boolean checkAnyHtpsswd(String auth, String fileName, String url) {
        File f = new File(repositoryPath + "/" + fileName+ "/.htpasswd");
        if(f.exists()) {
            if(!StringUtils.isBlank(auth)){
                return checkAuthentification(auth, f);
            }
            return true;
        }
        return false;
    }

    public OutputStream post(Request request) {
        return null;
    }
}
