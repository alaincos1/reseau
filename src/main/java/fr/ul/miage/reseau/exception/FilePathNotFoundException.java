package fr.ul.miage.reseau.exception;

import java.io.OutputStream;

public class FilePathNotFoundException extends ApiException {
    public FilePathNotFoundException(String file, OutputStream out) {
        super("FILE_NOT_FOUND_EXCEPTION", "Le fichier cible n'a pas été trouvé [path=\"" + file + "\"]", out);
    }
}
