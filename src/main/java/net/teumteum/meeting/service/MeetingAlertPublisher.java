package net.teumteum.meeting.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import net.teumteum.meeting.domain.BeforeMeetingAlerted;
import net.teumteum.meeting.domain.EndMeetingAlerted;
import net.teumteum.meeting.domain.MeetingRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingAlertPublisher {

    private static final String KR_TIME_ZONE = "Asia/Seoul";
    private static final String EVERY_ONE_MINUTES = "0 * * * * *";
    private static final String EVERY_12PM = "0 0 12 * * *";

    private final MeetingRepository meetingRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = EVERY_ONE_MINUTES)
    public void alertBeforeMeeting() {
        var alertStart = LocalDateTime.now(ZoneId.of(KR_TIME_ZONE)).plusMinutes(5).withNano(0).withSecond(0);
        var alertEnd = alertStart.plusMinutes(1).withNano(0).withSecond(0);
        var alertTargets = meetingRepository.findAlertMeetings(alertStart, alertEnd);
        alertTargets.stream()
            .filter(alertTarget -> alertTarget.getParticipantUserIds().size() > 2)
            .forEach(meeting -> eventPublisher.publishEvent(
                    new BeforeMeetingAlerted(meeting.getParticipantUserIds())
                )
            );
    }

    @Scheduled(cron = EVERY_12PM)
    public void alertEndMeeting() {
        var today = LocalDateTime.now(ZoneId.of(KR_TIME_ZONE))
            .withNano(0)
            .withSecond(0)
            .withMinute(0)
            .withHour(0);

        var yesterday = today.minusDays(1);

        var alertTargets = meetingRepository.findAlertMeetings(yesterday, today);
        alertTargets.stream()
            .filter(alertTarget -> alertTarget.getParticipantUserIds().size() > 2)
            .forEach(meeting -> eventPublisher.publishEvent(
                new EndMeetingAlerted(meeting.getId(), meeting.getTitle(), meeting.getParticipantUserIds())
            ));
    }

    @Scheduled(cron = EVERY_ONE_MINUTES)
    public void alertEndMeetingForQa() {
        var today = LocalDateTime.now(ZoneId.of(KR_TIME_ZONE))
            .withNano(0)
            .withSecond(0)
            .withMinute(0)
            .withHour(0);

        var future = today.plusDays(365);
        var yesterday = today.minusDays(365);

        var alertTargets = meetingRepository.findAlertMeetings(yesterday, future);
        alertTargets.stream()
            .filter(alertTarget -> alertTarget.getParticipantUserIds().size() > 2)
            .forEach(meeting -> eventPublisher.publishEvent(
                new EndMeetingAlerted(meeting.getId(), meeting.getTitle(), meeting.getParticipantUserIds())
            ));
    }
}
