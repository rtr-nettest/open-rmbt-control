package at.rtr.rmbt.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("app.rmbt")
public class ApplicationProperties {
    private LanguageProperties language;
    private Set<String> clientNames;
    private String version;
    private Integer threads;
    private Integer duration;
    private Integer pings;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LanguageProperties {
        private Set<String> supportedLanguages;
        private String defaultLanguage;
    }
}
