package at.rtr.rmbt.config;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.bedatadriven.jackson.datatype.jts.parsers.GeometryParser;
import com.bedatadriven.jackson.datatype.jts.parsers.PointParser;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson config class.
 */
@Configuration
public class JacksonConfig {

    /**
     * Jts module.
     *
     * @return the result
     */
    @Bean
    public JtsModule jtsModule() {
        return new JtsModule();
    }

    /**
     * Geometry parser.
     *
     * @return the result
     */
    @Bean
    public GeometryParser<Point> geometryParser() {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 900913);
        return new PointParser(geometryFactory);
    }
}
