package at.rtr.rmbt;

import at.rtr.rmbt.properties.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.specure.core", "at.rtr.rmbt"})
@EnableJpaRepositories(basePackages = {"com.specure.core.repository", "at.rtr.rmbt.repository"})
@EntityScan(basePackages = {"com.specure.core.model", "at.rtr.rmbt.model"})
@PropertySource({"classpath:git.properties"})
@EnableConfigurationProperties(ApplicationProperties.class)
public class RTRApplication {

    public static void main(String[] args) {
        SpringApplication.run(RTRApplication.class, args);
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:SystemMessages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
