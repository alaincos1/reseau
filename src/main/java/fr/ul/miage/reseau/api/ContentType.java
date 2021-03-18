package fr.ul.miage.reseau.api;

import fr.ul.miage.reseau.exception.ApiException;
import fr.ul.miage.reseau.exception.ContentTypeNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@AllArgsConstructor
public enum ContentType {
    HTML("html","text/html"),
    CSS("css","text/css"),
    JPG("jpg","image/jpeg"),
    JPEG("jpeg","image/jpeg"),
    PNG("png","image/png"),
    ICO("ico","image/x-icon"),
    GIF("gif","image/gif");

    private final String format;
    private final String type;

    public static String getContentType(String extension) throws ApiException {
        String type = Arrays.asList(values()).stream()
                .filter(contentType -> StringUtils.equalsAnyIgnoreCase(contentType.format, extension))
                .map(contentType -> contentType.type)
                .findFirst()
                .orElse(null);

        if (StringUtils.isBlank(type)) {
            throw new ContentTypeNotFoundException(extension);
        }
        return type;
    }
}
