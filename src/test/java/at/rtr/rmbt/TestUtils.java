package at.rtr.rmbt;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUtils {
    private final ObjectMapper mapper = new ObjectMapper();

    public static String asJsonString(final Object obj) throws JsonProcessingException {
        mapper.registerModule(new JtsModule());
        return mapper.writeValueAsString(obj);
    }
}