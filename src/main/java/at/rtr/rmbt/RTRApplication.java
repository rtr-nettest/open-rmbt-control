package at.rtr.rmbt;

import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.repository.impl.CustomRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"at.rtr.rmbt"})
@EnableJpaRepositories(basePackages = {"at.rtr.rmbt.repository"}, repositoryBaseClass = CustomRepositoryImpl.class)
@EntityScan(basePackages = {"at.rtr.rmbt.model"})
@PropertySource({"classpath:git.properties"})
@EnableConfigurationProperties(ApplicationProperties.class)
public class RTRApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(RTRApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RTRApplication.class);
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:SystemMessages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
