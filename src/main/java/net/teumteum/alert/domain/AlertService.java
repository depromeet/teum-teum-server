package net.teumteum.alert.domain;

import static net.teumteum.alert.domain.AlertExecutorConfigurer.ALERT_EXECUTOR;

import lombok.RequiredArgsConstructor;
import net.teumteum.meeting.domain.MeetingAlerted;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    private final AlertPublisher alertPublisher;
    private final AlertRepository alertRepository;

    @Async(ALERT_EXECUTOR)
    @EventListener({MeetingAlerted.class})
    public void alert(MeetingAlerted alerted) {
        alertRepository.findAllByUserId(alerted.userIds())
            .forEach(alertPublisher::publish);
    }
}
