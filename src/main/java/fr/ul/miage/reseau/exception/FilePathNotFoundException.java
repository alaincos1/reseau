package fr.ul.miage.reseau.exception;

import java.io.OutputStream;

public class FilePathNotFoundException extends ApiException {
    public FilePathNotFoundException(String file, OutputStream out) {
        super("FILE_PATH_NOT_FOUND_EXCEPTION", "Le fichier cible est introuvable [path=\"" + file + "\"]", out, HttpStatus.NOT_FOUND);
    }
}
