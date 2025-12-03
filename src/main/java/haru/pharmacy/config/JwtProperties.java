package haru.pharmacy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * Секретный ключ для шифрования токенов
     */
    private String secret;

    /**
     * Время жизни токена в миллисекундах (по умолчанию 24 часа)
     */
    private long expiration = 86400000; 
}