package net.teumteum.core.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "data.redis")
public class RedisProperty {
    private String host;
    private int port;
}
