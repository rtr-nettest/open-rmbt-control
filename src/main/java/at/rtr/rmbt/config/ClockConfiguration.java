package at.rtr.rmbt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Clock configuration class.
 */
@Configuration
public class ClockConfiguration {

    /**
     * Clock.
     *
     * @return the result
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

}
