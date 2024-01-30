package net.teumteum.alert.app;

import static net.teumteum.alert.app.AlertExecutorConfigurer.ALERT_EXECUTOR;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import net.teumteum.alert.domain.AlertPublisher;
import net.teumteum.alert.domain.AlertService;
import net.teumteum.alert.domain.BeforeMeetingAlert;
import net.teumteum.meeting.domain.MeetingAlerted;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class BeforeMeetingAlertHandler {

    private final AlertService alertService;
    private final AlertPublisher<BeforeMeetingAlert> alertPublisher;

    @Async(ALERT_EXECUTOR)
    @EventListener({MeetingAlerted.class})
    public void alert(MeetingAlerted alerted) {
        System.out.println(">>> alert(" + alerted.userIds() + ")");
        alertService.findAllByUserId(alerted.userIds())
            .stream()
            .map(userAlert -> new BeforeMeetingAlert(userAlert.getUserId(), userAlert.getToken(), Instant.now()))
            .forEach(alertPublisher::publish);
    }

}
