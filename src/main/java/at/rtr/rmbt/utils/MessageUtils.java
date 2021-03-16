package at.rtr.rmbt.utils;

import at.rtr.rmbt.properties.ApplicationProperties;
import lombok.experimental.UtilityClass;

import java.util.Locale;

@UtilityClass
public class MessageUtils {

    public Locale getLocaleFormLanguage(String language, ApplicationProperties.LanguageProperties languageProperties) {
        if (languageProperties.getSupportedLanguages().contains(language)) {
            return Locale.forLanguageTag(language);
        } else {
            return Locale.forLanguageTag(languageProperties.getDefaultLanguage());
        }
    }
}
