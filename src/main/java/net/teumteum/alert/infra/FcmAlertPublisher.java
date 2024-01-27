package net.teumteum.alert.infra;

import static net.teumteum.alert.infra.FcmAlertExecutorConfigurer.FCM_ALERT_EXECUTOR;

import net.teumteum.alert.domain.AlertPublisher;
import net.teumteum.alert.domain.Alertable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FcmAlertPublisher implements AlertPublisher {

    @Override
    @Async(FCM_ALERT_EXECUTOR)
    public void publish(Alertable alertable) {
        // TODO: 구현
    }
}
