package net.teumteum.alert.infra;

import static net.teumteum.alert.infra.FcmAlertExecutorConfigurer.FCM_ALERT_EXECUTOR;

import net.teumteum.alert.domain.Alert;
import net.teumteum.alert.domain.AlertPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class FcmAlertPublisher implements AlertPublisher {

    @Override
    @Async(FCM_ALERT_EXECUTOR)
    public void publish(Alert alert) {
        // TODO: FCM 연동 및 알림 구현 기능
    }
}
