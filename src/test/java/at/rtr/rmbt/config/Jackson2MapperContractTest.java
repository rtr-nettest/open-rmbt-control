package at.rtr.rmbt.config;

import at.rtr.rmbt.request.RtrSettingsRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jackson2.autoconfigure.Jackson2AutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that the Spring Boot auto-configured Jackson 2 ObjectMapper (the one
 * {@link WebMvcConfiguration} installs as the REST JSON converter), together with the
 * {@code spring.jackson2.*} settings in {@code application.yml} and the application's Jackson 2
 * Modules ({@link JacksonConfig}), honours the pre-Boot-4 REST contract: epoch timestamps, null
 * fields omitted, lenient UUID parsing, and no failure on a JSON null bound to a primitive.
 */
class Jackson2MapperContractTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Jackson2AutoConfiguration.class))
            .withUserConfiguration(JacksonConfig.class)
            .withPropertyValues(
                    "spring.jackson2.default-property-inclusion=non_null",
                    "spring.jackson2.serialization.write-dates-as-timestamps=true");

    @Test
    void datesAreSerializedAsEpochTimestamps() {
        runner.run(context -> {
            ObjectMapper mapper = context.getBean(ObjectMapper.class);
            String json = mapper.writeValueAsString(Map.of("t", ZonedDateTime.parse("2021-01-01T00:00:00Z")));
            assertThat(json).isEqualTo("{\"t\":1609459200.000000000}");
        });
    }

    @Test
    void nullFieldsAreOmitted() {
        runner.run(context -> {
            ObjectMapper mapper = context.getBean(ObjectMapper.class);
            Map<String, Object> m = new HashMap<>();
            m.put("a", null);
            m.put("b", 1);
            assertThat(mapper.writeValueAsString(m)).isEqualTo("{\"b\":1}");
        });
    }

    @Test
    void nullBoundToPrimitiveCoercesToDefault() {
        runner.run(context -> {
            ObjectMapper mapper = context.getBean(ObjectMapper.class);
            RtrSettingsRequest request = mapper.readValue(
                    "{\"name\":\"RMBT\",\"user_server_selection\":null}", RtrSettingsRequest.class);
            assertThat(request.isUserServerSelection()).isFalse();
        });
    }

    @Test
    void malformedUuidBecomesNullInsteadOfFailing() {
        runner.run(context -> {
            ObjectMapper mapper = context.getBean(ObjectMapper.class);
            RtrSettingsRequest request = mapper.readValue(
                    "{\"name\":\"RMBT\",\"uuid\":\"not-a-uuid\"}", RtrSettingsRequest.class);
            assertThat(request.getUuid()).isNull();
        });
    }
}
