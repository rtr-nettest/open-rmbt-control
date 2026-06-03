package at.rtr.rmbt.config;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * UUID generator class.
 */
@Component
public class UUIDGenerator {
    /**
     * Generate UUID.
     *
     * @return the result
     */
    public UUID generateUUID() {
        return UUID.randomUUID();
    }
}
