package haru.pharmacy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for JWT settings.
 * <p>
 * Maps properties from `application.properties` with the prefix `jwt`.
 * Example:
 * <pre>
 * jwt.secret=YourVeryLongSecretKey...
 * jwt.expiration=86400000
 * </pre>
 * </p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Secret key used for encryption and signing of tokens.
     * <p>
     * Must be at least 32 characters long to meet the 256-bit requirement for HMAC-SHA.
     * </p>
     */
    private String secret = "DefaultDevSecretKeyThatIsVeryLongAndSecureEnoughForLocalTesting123";

    /**
     * Token expiration time in milliseconds.
     * Default is 24 hours (86400000 ms).
     */
    private long expiration = 86400000;
}
