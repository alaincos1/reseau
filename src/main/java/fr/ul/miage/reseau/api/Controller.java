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

        try {
            in = new DataInputStream(new FileInputStream(filePath));
        } catch (FileNotFoundException exception) {
            log.error(exception.getMessage());
            throw new FilePathNotFoundException(FilenameUtils.getName(filePath), out);
        }
        log.debug("Chemin : " + filePath.toString());

        ContentType contentType = ContentType.getContentType(FilenameUtils.getExtension(request.getUrl()), out);
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

        Answer answerRequest = Answer.builder().request(request).contentLength(contentLength).contentType(contentType).httpStatus(httpStatus).content(content).build();
        sendAnswer(answerRequest);

        try {
            in.close();
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
    }

    public OutputStream post(Request request) {
        return null;
    }

    public void sendAnswer(Answer answer) {
        feedWrite("HTTP/1.1"+ answer.getHttpStatus().getValue() + " " + answer.getHttpStatus().getReasonPhrase() +"\r\n");
        feedWrite(("Content-Type: " + answer.getContentType().getType() + "\r\n"));
        feedWrite(("Content-Length: " + answer.getContentLength() + "\r\n"));
        feedWrite("\r\n");
        feedWrite(answer.getContent());
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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
