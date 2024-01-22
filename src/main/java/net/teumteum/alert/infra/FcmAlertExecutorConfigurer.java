package net.teumteum.alert.infra;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FcmAlertExecutorConfigurer {

    public static final String FCM_ALERT_EXECUTOR = "fcmAlertExecutor";

    @Bean
    public Executor fcmAlertExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

}
