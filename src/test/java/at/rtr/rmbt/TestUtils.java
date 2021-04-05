package at.rtr.rmbt;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {
    public static final ObjectMapper mapper = new ObjectMapper()
        .configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true)
        .configure(JsonParser.Feature.IGNORE_UNDEFINED, true)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .registerModule(new JavaTimeModule());

    public static String asJsonString(final Object obj) throws JsonProcessingException {
        mapper.registerModule(new JtsModule());
        return mapper.writeValueAsString(obj);
    }
}