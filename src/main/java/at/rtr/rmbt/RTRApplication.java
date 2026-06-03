package at.rtr.rmbt;

import at.rtr.rmbt.properties.ApplicationProperties;
import at.rtr.rmbt.properties.CleanupTaskProperties;
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
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"at.rtr.rmbt"})
@EnableJpaRepositories(basePackages = {"at.rtr.rmbt.repository"}, repositoryBaseClass = CustomRepositoryImpl.class)
@EntityScan(basePackages = {"at.rtr.rmbt.model"})
@EnableScheduling
@EnableConfigurationProperties({ApplicationProperties.class, CleanupTaskProperties.class})
// Run/Debug this class for debugging (e.g. in IntelliG)
public class RTRApplication extends SpringBootServletInitializer {

    static {
        // In a servlet container the webapp - not the JVM - owns dnsjava's "NIO selector" thread.
        // Disabling dnsjava's own JVM shutdown hook avoids the hook/close-thread interplay that, on a
        // full JVM stop, leaves that thread lingering past Tomcat's classloader-leak check (it can't
        // remove its shutdown hook while the JVM is already shutting down). ShutdownThreadCleaner still
        // stops the selector via NioClient.close() on context close (covering hot redeploys).
        // Set here so it takes effect before dnsjava's NioClient is first initialised.
        System.setProperty("dnsjava.nio.register_shutdown_hook", "false");
    }

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
