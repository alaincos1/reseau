package fr.ul.miage.api;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

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

    public static String getContentType(String extension) {
        for (ContentType content : values()) {
            if (StringUtils.equals(content.format,extension)) {
                return content.type;
            }
        }
        return null;
    }
}
