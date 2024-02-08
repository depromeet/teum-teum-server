package net.teumteum.alert.app;

import static net.teumteum.alert.app.AlertExecutorConfigurer.ALERT_EXECUTOR;

import lombok.RequiredArgsConstructor;
import net.teumteum.alert.domain.Alert;
import net.teumteum.alert.domain.AlertPublisher;
import net.teumteum.alert.domain.AlertService;
import net.teumteum.alert.domain.AlertType;
import net.teumteum.alert.domain.UserAlertService;
import net.teumteum.meeting.domain.MeetingAlerted;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class AlertHandler {

    private final UserAlertService userAlertService;
    private final AlertService alertService;
    private final AlertPublisher alertPublisher;

    @Async(ALERT_EXECUTOR)
    @EventListener(MeetingAlerted.class)
    public void handleBeforeMeetingAlerts(MeetingAlerted alerted) {
        userAlertService.findAllByUserId(alerted.userIds())
            .stream()
            .map(userAlert -> Pair.of(userAlert.getToken(),
                new Alert(null, userAlert.getUserId(), AlertType.BEFORE_MEETING.getTitle(),
                    AlertType.BEFORE_MEETING.getBody(), AlertType.BEFORE_MEETING)))
            .map(tokenAndAlert -> Pair.of(tokenAndAlert.getFirst(), alertService.save(tokenAndAlert.getSecond())))
            .forEach(tokenAndAlert -> alertPublisher.publish(tokenAndAlert.getFirst(), tokenAndAlert.getSecond()));
    }

}
