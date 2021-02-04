package at.rtr.rmbt;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {
    private final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    public static String asJsonString(final Object obj) throws JsonProcessingException {
        mapper.registerModule(new JtsModule());
        return mapper.writeValueAsString(obj);
    }
}