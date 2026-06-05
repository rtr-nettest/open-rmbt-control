package at.rtr.rmbt.config;

import at.rtr.rmbt.utils.LenientUuidDeserializer;
import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.bedatadriven.jackson.datatype.jts.parsers.GeometryParser;
import com.bedatadriven.jackson.datatype.jts.parsers.PointParser;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class JacksonConfig {

    @Bean
    public JtsModule jtsModule() {
        return new JtsModule();
    }

    /**
     * Registers {@link LenientUuidDeserializer} for every {@link UUID}, so any malformed/blank
     * client-supplied UUID in a request body is filtered to {@code null} instead of aborting the
     * request with an {@code InvalidFormatException} (e.g. {@code TestResultRequest.test_uuid}).
     * Spring Boot auto-detects Jackson {@code Module} beans and applies them to the ObjectMapper.
     */
    @Bean
    public com.fasterxml.jackson.databind.Module lenientUuidModule() {
        final SimpleModule module = new SimpleModule("lenient-uuid");
        module.addDeserializer(UUID.class, new LenientUuidDeserializer());
        return module;
    }

    @Bean
    public GeometryParser<Point> geometryParser() {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 900913);
        return new PointParser(geometryFactory);
    }
}
