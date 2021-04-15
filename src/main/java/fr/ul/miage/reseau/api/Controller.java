package fr.ul.miage.reseau.api;

import fr.ul.miage.reseau.enumutils.ContentType;
import fr.ul.miage.reseau.exception.ApiException;
import fr.ul.miage.reseau.exception.FilePathNotFoundException;
import fr.ul.miage.reseau.exception.ForbiddenException;
import fr.ul.miage.reseau.enumutils.HttpStatus;
import fr.ul.miage.reseau.communication.Response;
import fr.ul.miage.reseau.communication.Request;
import fr.ul.miage.reseau.html.HtmlGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
@AllArgsConstructor
public class Controller {
    private final OutputStream out;
    private final String repositoryPath;
    private final HashMap<String, String> domains;
    private final int repositoryMenu;

    public void dispatch(Request request) {
        switch (request.getMethod()) {
            case "GET":
                try {
                    get(request);
                } catch (ApiException exception) {
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
        String fileName = getFileName(request.getHost());
        String newUrl = deleteDomain(request.getUrl(), request.getHost());
        String filePath = repositoryPath + "/" + fileName + newUrl;
        Path path = Paths.get(filePath);

        DataInputStream in = null;
        ContentType contentType = null;
        HttpStatus httpStatus = null;
        byte[] content = null;
        int contentLength = 0;

        if(StringUtils.isBlank(FilenameUtils.getExtension(newUrl)) && repositoryMenu == 1){
            //TODO: dans une méthode

            contentType = ContentType.HTML;
            httpStatus = HttpStatus.OK;
            File folder = new File(filePath);
            if (folder.isDirectory()) {
                List<File> files = Arrays.asList(folder.listFiles().clone());
                content = HtmlGenerator.renderPathFinderHtml(files, request.getHost() + newUrl).getBytes();
                contentLength = content.length;
            }
            else {
                throw new FilePathNotFoundException(FilenameUtils.getName(filePath), out);
            }

        }else {
            //TODO: dans une méthode

            try {
                in = new DataInputStream(new FileInputStream(filePath));
            } catch (FileNotFoundException exception) {
                log.error(exception.getMessage());
                throw new FilePathNotFoundException(FilenameUtils.getName(filePath), out);
            }
            log.debug("Chemin : " + path.toString());

            try {
                contentType = ContentType.getContentType(FilenameUtils.getExtension(newUrl), out);
            } catch (ApiException e) {
                log.error(e.getMessage());
                return;
            }

            try {
                content = Files.readAllBytes(path);
                httpStatus = HttpStatus.OK;
            } catch (IOException exception) {
                log.error(exception.getMessage());
            }

            try {
                contentLength = in.available();
                in.close();
            } catch (IOException exception) {
                log.error(exception.getMessage());
            }
        }

        //TODO: dans une méthode
        StringBuilder url = new StringBuilder();
        url.append(fileName);
        for (int i = 0; i < newUrl.split("/").length - 1; i++) {
            url.append("/").append(newUrl.split("/")[i]);
        }
        boolean authRequired = checkAuthRecursiv(request.getAuthorization(), url.toString(), 1);

        //TODO: dans une méthode
        if(authRequired && !StringUtils.isBlank(request.getAuthorization())) {
            throw new ForbiddenException(fileName + newUrl, out);
        }
        else if(authRequired && StringUtils.isBlank(request.getAuthorization())) {
                Response responseRequest = Response.builder()
                        .contentLength(contentLength)
                        .contentType(contentType)
                        .httpStatus(HttpStatus.UNAUTHORIZED)
                        .content("".getBytes())
                        .out(out)
                        .authenticate("Basic realm=\"Authentification\"")
                        .build();
                responseRequest.send();
        }
        else{
            Response responseRequest = Response.builder()
                    .contentLength(contentLength)
                    .contentType(contentType)
                    .httpStatus(httpStatus)
                    .content(content)
                    .out(out)
                    .build();
            responseRequest.send();
        }
    }

    private String deleteDomain(String url, String host) {
        if(url.startsWith("/" + host)){
            return url.replaceFirst("/" + host,"");
        }
        return url;
    }

    private String getFileName(String host) {
        return domains.get(host);
    }

    // count = 1 à l'appel  et  path = filename + "/" + url MAIS SANS /file.ext
    private boolean checkAuthRecursiv(String auth, String path, int count) {
        if(path.split("/").length == count-1){
            return false;
        }
        else {
            StringBuilder pathTemp = new StringBuilder();
            for(int i = 0; i<count; i++){
                pathTemp.append("/").append(path.split("/")[i]);
            }
            File f = new File(repositoryPath + "/" + pathTemp + "/.htpasswd");
            if (!f.exists()) { // le htpsswd n'existe pas
                return checkAuthRecursiv(auth,path,count+1);
            }
            else { // le htpsswd existe
                if (!StringUtils.isBlank(auth)) {
                    if(checkAuthentification(auth, f)){
                        return true;
                    }
                    return checkAuthRecursiv(auth,path,count+1);
                }
                return true;
            }
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

    public OutputStream post(Request request) {
        return null;
    }
}
