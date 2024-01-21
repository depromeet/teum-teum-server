package net.teumteum.meeting.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import net.teumteum.meeting.domain.MeetingRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingAlertService {

    private final MeetingRepository meetingRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 * * * * *")
    private void alertMeeting() {
        var alertStart = LocalDateTime.now().plusMinutes(5);
        var alertEnd = alertStart.plusMinutes(1);
        var alertTargets = meetingRepository.findAlertMeetings(alertStart, alertEnd);
        alertTargets.forEach(meeting -> eventPublisher.publishEvent(meeting.getParticipantUserIds()));
    }
}
