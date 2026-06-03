package at.rtr.rmbt.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Open api configuration class.
 */
@Configuration
public class OpenApiConfiguration {

    /**
     * Open API.
     *
     * @return the result
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Control server")
                        .description("Software project")
                        .version("1.0"));
    }
}
