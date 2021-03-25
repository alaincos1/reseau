package fr.ul.miage.api;

import fr.ul.miage.exception.ApiException;
import fr.ul.miage.exception.ContentTypeNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public enum ContentType {
    HTML("html","text/html"),
    CSS("css","text/html"),
    JPG("jpg","image/jpeg"),
    JPEG("jpeg","image/jpeg"),
    PNG("png","image/png"),
    ICO("ico","image/x-icon"),
    GIF("gif","image/gif");

    private final String format;
    private final String type;

    public static String getContentType(String extension, OutputStream out) throws ApiException {
        return Arrays.asList(values()).stream()
                .filter(contentType -> StringUtils.equalsAnyIgnoreCase(contentType.format, extension))
                .map(contentType -> contentType.type)
                .findFirst()
                .orElseThrow(() -> new ContentTypeNotFoundException(extension, out));
    }
}
