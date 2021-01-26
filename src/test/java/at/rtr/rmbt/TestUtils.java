package at.rtr.rmbt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {
    private final ObjectMapper mapper = new ObjectMapper();

    public static String asJsonString(final Object obj) throws JsonProcessingException {
            return mapper.writeValueAsString(obj);
    }
}