package fr.ul.miage.reseau.parser;

import fr.ul.miage.reseau.exception.InvalidRequestException;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;
import java.util.List;

@Getter
@ToString
@Builder
public class Request {
    private final String method;
    private final String url;
    private final String version;
    private final String host;
    private final String authorization;
    private final OutputStream out;

    private final List<String> list;

    public static RequestBuilder builder() {
        return new CustomRequestBuilder();
    }

    public static class CustomRequestBuilder extends RequestBuilder {
        public Request build() {
            super.list.stream().forEach(line -> {
                if (line.startsWith("GET")) {
                    String[] splittedRequest = line.split(" ");
                    super.method = splittedRequest[0];
                    super.url = splittedRequest[1].equals("/") ? "/index.html" : splittedRequest[1];
                    super.version = splittedRequest[2];
                } else if (line.contains("Host: ")) {
                    super.host = line.split(": ")[1];
                } else if (line.contains("Authorization: ")) {
                    super.authorization = line.split(" ")[2];
                }
            });

            if (StringUtils.isBlank(super.host)
                    || StringUtils.isBlank(super.method)
                    || StringUtils.isBlank(super.url)
                    || StringUtils.isBlank(super.version)) {
                throw new InvalidRequestException(super.out);
            }

            return super.build();
        }
    }
}
