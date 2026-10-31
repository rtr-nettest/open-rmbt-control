package at.rtr.rmbt.config;

import at.rtr.rmbt.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies {@link WebMvcConfiguration#extendMessageConverters(List)}: a Jackson 2 converter is placed
 * first so it wins for JSON over the Framework 7 default Jackson 3 converter (converters are tried in
 * order), while the existing converters are kept.
 */
class WebMvcConfigurationConverterTest {

    @Test
    void prependsJackson2SoItWinsForJson() {
        WebMvcConfiguration config = new WebMvcConfiguration(TestUtils.mapper);

        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        converters.add(new JacksonJsonHttpMessageConverter()); // the Boot 4 / Framework 7 default

        config.extendMessageConverters(converters);

        // The first converter able to read JSON is the Jackson 2 one, so it wins over Jackson 3.
        HttpMessageConverter<?> firstJson = converters.stream()
                .filter(c -> c.canRead(Object.class, MediaType.APPLICATION_JSON))
                .findFirst()
                .orElseThrow();
        assertThat(firstJson).isInstanceOf(MappingJackson2HttpMessageConverter.class);
        // Pre-existing converters are preserved.
        assertThat(converters).anyMatch(c -> c instanceof ByteArrayHttpMessageConverter);
        assertThat(converters).anyMatch(c -> c instanceof StringHttpMessageConverter);
        assertThat(converters).anyMatch(c -> c instanceof JacksonJsonHttpMessageConverter);
    }
}
