package fr.ul.miage.reseau.api;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;

@AllArgsConstructor
public enum Domain {
    verti("verti.miage","verti"),
    dopetrope("dopetrope.miage","dopetrope"),
    unicorn("unicorn.miage","unicorn");

    private final String url;
    private final String file;

    public static String getFileName(String host) {
        return Arrays.asList(values()).stream()
                .filter(domain -> StringUtils.equalsAnyIgnoreCase(domain.url, host))
                .map(domain -> domain.file)
                .findFirst()
                .orElse(null);
    }
}
