package fr.ul.miage.reseau.parser;

import fr.ul.miage.reseau.exception.InvalidRequestException;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.OutputStream;

@Getter
@ToString
@Builder
public class Request {
    private final String method;
    private final String url;
    private final String version;
    private final String host;
    private final OutputStream out;

    private final String rawRequest;
    private final String rawHost;

    public static RequestBuilder builder() {
        return new CustomRequestBuilder();
    }

    public static class CustomRequestBuilder extends RequestBuilder {
        public Request build() {
            if (StringUtils.isBlank(super.rawHost)
                    || StringUtils.isBlank(super.rawRequest)
                    || super.out == null) {
                throw new InvalidRequestException(super.out);
            }

            String[] splittedRequest = super.rawRequest.split(" ");
            super.host = super.rawHost.split(": ")[1];
            super.method = splittedRequest[0];
            super.url = splittedRequest[1].equals("/") ? "/index.html" : splittedRequest[1];
            super.version = splittedRequest[2];
            super.url = (super.url.equals("/") ? "/index.html" : super.url);

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
