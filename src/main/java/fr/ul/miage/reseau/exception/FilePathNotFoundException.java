package fr.ul.miage.reseau.exception;

import fr.ul.miage.reseau.enumutils.HttpStatus;

import java.io.OutputStream;

public class FilePathNotFoundException extends ApiException {
    public FilePathNotFoundException(String file, OutputStream out) {
        super("FILE_PATH_NOT_FOUND_EXCEPTION", "Target file is not found [path=\"" + file + "\"]", out, HttpStatus.NOT_FOUND);
    }
}
