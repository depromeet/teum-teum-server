package net.teumteum.alert.domain;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlertExecutorConfigurer {

    public static final String ALERT_EXECUTOR = "alertExecutor";

    @Bean
    public Executor alertExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

}
