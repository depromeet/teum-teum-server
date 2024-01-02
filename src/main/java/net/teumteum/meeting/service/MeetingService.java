package net.teumteum.meeting.service;

import lombok.RequiredArgsConstructor;
import net.teumteum.meeting.domain.Meeting;
import net.teumteum.meeting.domain.MeetingRepository;
import net.teumteum.meeting.domain.MeetingSpecification;
import net.teumteum.meeting.domain.Topic;
import net.teumteum.meeting.domain.response.MeetingResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
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

    @Transactional(readOnly = true)
    public Slice<Meeting> getMeetingsBySpecification(Pageable pageable, Topic topic, String meetingAreaStreet,
                                                     Long participantUserId, String searchWord, boolean isOpen) {

        Specification<Meeting> spec = MeetingSpecification.withIsOpen(isOpen);

        if (topic != null) spec = spec.and(MeetingSpecification.withTopic(topic));
        else if (meetingAreaStreet != null) spec.and(MeetingSpecification.withAreaStreet(meetingAreaStreet));
        else if (participantUserId != null)
            spec = spec.and(MeetingSpecification.withParticipantUserId(participantUserId));
        else if (searchWord != null)
            spec = MeetingSpecification.withSearchWordInTitle(searchWord).or(MeetingSpecification.withSearchWordInIntroduction(searchWord))
                    .and(MeetingSpecification.withIsOpen(isOpen));

        return meetingRepository.findAll(spec, pageable);
    }

}
