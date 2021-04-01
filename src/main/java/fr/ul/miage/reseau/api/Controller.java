package fr.ul.miage.reseau.api;

import fr.ul.miage.reseau.exception.FilePathNotFoundException;
import fr.ul.miage.reseau.exception.HttpStatus;
import fr.ul.miage.reseau.parser.Answer;
import fr.ul.miage.reseau.parser.Request;
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
        String filePath = repositoryPath + "/" +fileName + request.getUrl();
        Path path = Paths.get(filePath);
        DataInputStream in = null;

        ContentType contentType = ContentType.getContentType(FilenameUtils.getExtension(request.getUrl()), out);

        try {
            in = new DataInputStream(new FileInputStream(filePath));
        } catch (FileNotFoundException exception) {
            log.error(exception.getMessage());
            throw new FilePathNotFoundException(FilenameUtils.getName(filePath), out);
        }
        log.debug("Chemin : " + path.toString());


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

        Answer answerRequest = Answer.builder()
                .contentLength(contentLength)
                .contentType(contentType)
                .httpStatus(httpStatus)
                .content(content)
                .out(out)
                .build();
        answerRequest.send();

        try {
            in.close();
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }

    public OutputStream post(Request request) {
        return null;
    }
}
