package net.teumteum.meeting.service;

import lombok.RequiredArgsConstructor;
import net.teumteum.meeting.domain.MeetingRepository;
import net.teumteum.meeting.domain.response.MeetingResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;

    @Transactional(readOnly = true)
    public MeetingResponse getMeetingById(Long meetingId) {
        var existMeeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("meetingId에 해당하는 모임을 찾을 수 없습니다. \"" + meetingId + "\""));

        return MeetingResponse.of(existMeeting);
    }

}
