package fr.ul.miage.reseau.api;

import fr.ul.miage.reseau.exception.ApiException;
import fr.ul.miage.reseau.exception.ContentTypeNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;
import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum ContentType {
    HTML("html","text/html"),
    CSS("css","text/css"),
    JS("js","text/javascript"),
    JPG("jpg","image/jpeg"),
    JPEG("jpeg","image/jpeg"),
    PNG("png","image/png"),
    ICO("ico","image/x-icon"),
    GIF("gif","image/gif"),
    TTF("ttf","font/ttf"),
    WOFF("woff","font/woff"),
    WOFF2("woff2","font/woff2");

    private final String format;
    private final String type;

    public static String getType(String extension, OutputStream out) throws ApiException {
        return Arrays.asList(values()).stream()
                .filter(contentType -> StringUtils.equalsAnyIgnoreCase(contentType.format, extension))
                .map(contentType -> contentType.type)
                .findFirst()
                .orElseThrow(() -> new ContentTypeNotFoundException(extension, out));
    }

    public static ContentType getContentType(String extension, OutputStream out) throws ApiException {
        return Arrays.asList(values()).stream()
                .filter(contentType -> StringUtils.equalsAnyIgnoreCase(contentType.format, extension))
                .findFirst()
                .orElseThrow(() -> new ContentTypeNotFoundException(extension, out));
    }
}
