package fr.ul.miage.api;

import fr.ul.miage.exception.ApiException;
import fr.ul.miage.exception.FilePathNotFoundException;
import fr.ul.miage.parser.Request;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@AllArgsConstructor
public class Controller {
    private static final Path ERROR_404 = Paths.get("src/main/resources/404.jpg");
    private OutputStream out;

    public OutputStream dispatch(Request request) {
        switch (request.getCommand()) {
            case "GET":
                get(request);
                break;
            case "POST":
                post(request);
                break;
            default:
                log.error("Commande incorrecte");
        }
        return out;
    }

    public void get(Request request) {
        String resourcesPath = "src/main/resources";
        String filePath = resourcesPath + request.getUrl();
        log.debug(filePath);
        Path path = Paths.get(filePath);

        DataInputStream in = null;
        try {
            in = new DataInputStream(new FileInputStream(System.getProperty("user.dir") + "/" + filePath));
        } catch (FileNotFoundException exception) {
            log.error(exception.getMessage());
            throw new FilePathNotFoundException(FilenameUtils.getName(filePath), out);
        }
        log.debug("Chemin : " + path.toString());
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
            feedWrite("HTTP/1.1 200 OK\r\n");
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }

        String contentType = null;
        try {
            contentType = ContentType.getContentType(FilenameUtils.getExtension(request.getUrl()), out);
        } catch (ApiException e) {
            log.error(e.getMessage());
            return;
        }

        feedWrite(("Content-Type: " + contentType + "\r\n"));

        try {
            feedWrite(("Content-Length: " + in.available() + "\r\n"));
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }

        feedWrite("\r\n");
        feedWrite(content);

        try {
            in.close();
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }

    public OutputStream post(Request request) {
        return null;
    }

    public void feedWrite(byte[] data) {
        try {
            out.write(data);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void feedWrite(String data) {
        feedWrite(data.getBytes());
    }

}
