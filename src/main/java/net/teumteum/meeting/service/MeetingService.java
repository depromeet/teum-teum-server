package net.teumteum.meeting.service;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import net.teumteum.meeting.domain.ImageUpload;
import net.teumteum.meeting.domain.Meeting;
import net.teumteum.meeting.domain.MeetingArea;
import net.teumteum.meeting.domain.MeetingRepository;
import net.teumteum.meeting.domain.MeetingSpecification;
import net.teumteum.meeting.domain.Topic;
import net.teumteum.meeting.domain.request.CreateMeetingRequest;
import net.teumteum.meeting.domain.response.MeetingResponse;
import net.teumteum.meeting.domain.response.MeetingsResponse;
import net.teumteum.meeting.model.PageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final ImageUpload imageUpload;

    @Transactional
    public MeetingResponse createMeeting(List<MultipartFile> images, CreateMeetingRequest meetingRequest, Long userId) {
        Assert.isTrue(!images.isEmpty() && images.size() <= 5, "이미지는 1개 이상 5개 이하로 업로드해야 합니다.");

        Meeting meeting = meetingRepository.save(
            Meeting.builder()
                .hostUserId(userId)
                .title(meetingRequest.title())
                .topic(meetingRequest.topic())
                .introduction(meetingRequest.introduction())
                .meetingArea(MeetingArea.of(
                    meetingRequest.meetingArea().address(),
                    meetingRequest.meetingArea().addressDetail())
                )
                .numberOfRecruits(meetingRequest.numberOfRecruits())
                .promiseDateTime(meetingRequest.promiseDateTime())
                .participantUserIds(Set.of(userId))
                .build()
        );

        uploadMeetingImages(images, meeting);

        return MeetingResponse.of(meeting);
    }

    @Transactional(readOnly = true)
    public MeetingResponse getMeetingById(Long meetingId) {
        var existMeeting = getMeeting(meetingId);

        return MeetingResponse.of(existMeeting);
    }

    @Transactional
    public void deleteMeeting(Long meetingId, Long userId) {
        var existMeeting = getMeeting(meetingId);

        if (!existMeeting.isHost(userId)) {
            throw new IllegalArgumentException("모임을 삭제할 권한이 없습니다.");
        }
        if (!existMeeting.isOpen()) {
            throw new IllegalArgumentException("종료된 모임은 삭제할 수 없습니다.");
        }

        meetingRepository.delete(existMeeting);
    }

    @Transactional(readOnly = true)
    public PageDto<MeetingsResponse> getMeetingsBySpecification(Pageable pageable, Topic topic,
        String meetingAreaStreet,
        Long participantUserId, String searchWord, boolean isOpen) {

        Specification<Meeting> spec = MeetingSpecification.withIsOpen(isOpen);

        if (topic != null) {
            spec = spec.and(MeetingSpecification.withTopic(topic));
        } else if (meetingAreaStreet != null) {
            spec.and(MeetingSpecification.withAreaStreet(meetingAreaStreet));
        } else if (participantUserId != null) {
            spec = spec.and(MeetingSpecification.withParticipantUserId(participantUserId));
        } else if (searchWord != null) {
            spec = MeetingSpecification.withSearchWordInTitle(searchWord)
                .or(MeetingSpecification.withSearchWordInIntroduction(searchWord))
                .and(MeetingSpecification.withIsOpen(isOpen));
        }

        var meetings = meetingRepository.findAll(spec, pageable);

        return PageDto.of(MeetingsResponse.of(meetings.getContent()), meetings.hasNext());
    }

    @Transactional
    public MeetingResponse addParticipant(Long meetingId, Long userId) {
        var existMeeting = getMeeting(meetingId);

        if (existMeeting.alreadyParticipant(userId)) {
            throw new IllegalArgumentException("이미 참여한 모임입니다.");
        }

        if (!existMeeting.isOpen()) {
            throw new IllegalArgumentException("모임 참여 기간이 종료되었습니다.");
        }

        existMeeting.addParticipant(userId);
        return MeetingResponse.of(existMeeting);
    }

    @Transactional
    public void cancelParticipant(Long meetingId, Long userId) {
        var existMeeting = getMeeting(meetingId);

        if (!existMeeting.isOpen()) {
            throw new IllegalArgumentException("종료된 모임에서 참여를 취소할 수 없습니다.");
        }

        if (!existMeeting.alreadyParticipant(userId)) {
            throw new IllegalArgumentException("참여하지 않은 모임입니다.");
        }

        existMeeting.cancelParticipant(userId);
    }

    private void uploadMeetingImages(List<MultipartFile> images, Meeting meeting) {
        images.forEach(
            image -> meeting.getImageUrls().add(
                imageUpload.upload(image, meeting.getId().toString()).filePath()
            )
        );
    }

    private Meeting getMeeting(Long meetingId) {
        return meetingRepository.findById(meetingId)
            .orElseThrow(() -> new IllegalArgumentException("meetingId에 해당하는 모임을 찾을 수 없습니다. \"" + meetingId + "\""));
    }
}
