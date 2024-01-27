package net.teumteum.alert.app;

import static net.teumteum.alert.app.AlertExecutorConfigurer.ALERT_EXECUTOR;

import lombok.RequiredArgsConstructor;
import net.teumteum.alert.domain.AlertPublisher;
import net.teumteum.alert.domain.AlertService;
import net.teumteum.alert.domain.BeforeMeetingAlert;
import net.teumteum.meeting.domain.MeetingAlerted;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingAlertHandler {

    private final AlertService alertService;
    private final AlertPublisher alertPublisher;

    @Async(ALERT_EXECUTOR)
    @EventListener({MeetingAlerted.class})
    public void alert(MeetingAlerted alerted) {
        alertService.findAllByUserId(alerted.userIds())
            .stream()
            .map(userAlert -> new BeforeMeetingAlert(userAlert.getUserId(), userAlert.getToken()))
            .forEach(alertPublisher::publish);
    }

}
