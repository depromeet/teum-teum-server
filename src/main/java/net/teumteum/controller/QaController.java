package net.teumteum.controller;

import lombok.RequiredArgsConstructor;
import net.teumteum.meeting.domain.EndMeetingAlerted;
import net.teumteum.meeting.service.MeetingService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QaController {

    private final MeetingService meetingService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping("/forcereviewalarm")
    public void forcereviewalarm(@RequestParam("meetingId") Long meetingId,
        @RequestParam("userId") Long userId) {
        var meeting = meetingService.getMeetingById(meetingId, userId);
        eventPublisher.publishEvent(new EndMeetingAlerted(meeting.id(), meeting.title(), meeting.participantIds()));
    }
}
