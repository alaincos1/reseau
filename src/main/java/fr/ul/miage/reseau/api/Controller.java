package fr.ul.miage.reseau.api;

import fr.ul.miage.reseau.enumutils.ContentType;
import fr.ul.miage.reseau.exception.ApiException;
import fr.ul.miage.reseau.exception.FilePathNotFoundException;
import fr.ul.miage.reseau.exception.ForbiddenException;
import fr.ul.miage.reseau.enumutils.HttpStatus;
import fr.ul.miage.reseau.communication.Response;
import fr.ul.miage.reseau.communication.Request;
import fr.ul.miage.reseau.html.HtmlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;

@Slf4j
public class Controller {
    private final OutputStream out;
    private final String repositoryPath;
    private final HashMap<String, String> domains;
    private final int repositoryMenu;
    private DataInputStream in = null;
    private ContentType contentType = null;
    private HttpStatus httpStatus = null;
    private byte[] content = null;
    private int contentLength = 0;

    public Controller(OutputStream out, String repositoryPath, HashMap<String, String> domains, int repositoryMenu){
        this.out = out;
        this.repositoryPath = repositoryPath;
        this.domains = domains;
        this.repositoryMenu = repositoryMenu;
    }

    /*
     * Oriente la requête en fonction de sa méthode (POST ou GET).
     *
     * @param request la requête reçue
     */
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

    /*
     * Processus de réponse à la requête GET
     *
     * @param request la requête reçue
     */
    public void get(Request request) {
        String fileName = domains.get(request.getHost());
        String newUrl = request.getUrl();
        String filePath = repositoryPath + "/" + fileName + newUrl;

        //Si le fichier demandé est un répertoire et que le paramètre de génération de répertoire est activé dans les properties
        //On génère une page html donnant accès à un menu des fichiers dans ce répertoire
        if(StringUtils.isBlank(FilenameUtils.getExtension(newUrl)) && repositoryMenu == 1){
            repositoryMenuGeneration(filePath, request.getHost(), newUrl);
        }else {
            //Sinon on génère une réponse normale
            answerGeneration(filePath, newUrl);
        }

        //recherche dans le chemin accédant au fichier demandé si une authentification est nécessaire (présence d'un .htpasswd)
        boolean authRequired = findAuthentification(fileName, newUrl, request);

        //si l'utilisateur est connecté avec un compte qui n'a pas les autorisations, envoi d'une exception 403
        if(authRequired && !StringUtils.isBlank(request.getAuthorization())) {
            throw new ForbiddenException(fileName + newUrl, out);
        }//si l'utilisateur n'est pas connecté, envoi d'une demande d'authentification 401
        else if(authRequired && StringUtils.isBlank(request.getAuthorization())) {
            sendAuthentificationRequest();
        }//s'il n'y a pas besoin de s'authentifier, envoi d'une réponse 200
        else{
            sendNormalRequest();
        }
    }

    /*
     * Construction et envoi de la réponse à la requête
     *
     */
    private void sendNormalRequest() {
        Response responseRequest = Response.builder()
                .contentLength(contentLength)
                .contentType(contentType)
                .httpStatus(httpStatus)
                .content(content)
                .out(out)
                .build();
        responseRequest.send();
    }

    /*
     * Construction et envoi de la réponse à la requête en cas de demande d'authentification
     *
     */
    private void sendAuthentificationRequest() {
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

    /*
     * Recherche d'un fichier nécessitant une authentification
     *
     * @param fileName newUrl request
     * @return value authentification nécessaire ou non
     */
    private boolean findAuthentification(String fileName, String newUrl, Request request) {
        StringBuilder url = new StringBuilder();
        url.append(fileName);
        for (int i = 0; i < newUrl.split("/").length - 1; i++) {
            url.append("/").append(newUrl.split("/")[i]);
        }
        return checkAuthRecursiv(request.getAuthorization(), url.toString(), 1);
    }

    /*
     * Recherche récursive sur l'ensemble des répertoires du chemin de l'existence d'un .htpasswd
     *
     * @param auth les informations de login encodé en base64
     * @param path le chemin parcouru
     * @param count le niveau dans le chemin
     * @return value authentification nécessaire ou non
     */
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

    /*
     * Vérifie les identifiants de connexion en fonction du fichier .htpasswd
     *
     * @param auth les informations de login encodé en base64
     * @param f le fichier .htpasswd
     * @return value authentification nécessaire ou non
     */
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

    /*
     * Génère les composantes de la réponse au serveur
     *
     * @param filePath newUrl
     */
    private void answerGeneration(String filePath, String newUrl) {
        Path path = Paths.get(filePath);
        try {
            in = new DataInputStream(new FileInputStream(filePath));
        } catch (FileNotFoundException exception) {
            log.error(exception.getMessage());
            throw new FilePathNotFoundException(FilenameUtils.getName(filePath), out);
        }
        log.debug("Chemin : " + path.toString());
        //TODO: à tester
        contentType = ContentType.getContentType(FilenameUtils.getExtension(newUrl), out);

        try {
            content = Files.readAllBytes(path);
            httpStatus = HttpStatus.OK;
            contentLength = in.available();
            in.close();
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }

    /*
     * Génération de la page html pour le menu des fichiers du répertoire
     *
     * @param filePath host newUrl
     */
    private void repositoryMenuGeneration(String filePath,String host,String newUrl) {
        contentType = ContentType.HTML;
        httpStatus = HttpStatus.OK;
        File folder = new File(filePath);
        if (folder.isDirectory()) {
            List<File> files = Arrays.asList(folder.listFiles().clone());
            String urlModified = host + newUrl;
            content = HtmlGenerator.renderPathFinderHtml(files, urlModified, urlModified.replace("/"+folder.getName(), "")).getBytes();
            contentLength = content.length;
        }
        else {
            throw new FilePathNotFoundException(FilenameUtils.getName(filePath), out);
        }
    }

    /*
     * Processus de réponse à la requête POST
     *
     * @param request la requête reçue
     */
    public OutputStream post(Request request) {
        log.info("Not implemented yet");
        return null;
    }
}
