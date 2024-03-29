package net.teumteum.meeting.controller;

import io.sentry.Sentry;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.core.security.service.SecurityService;
import net.teumteum.meeting.domain.Topic;
import net.teumteum.meeting.domain.request.CreateMeetingRequest;
import net.teumteum.meeting.domain.request.UpdateMeetingRequest;
import net.teumteum.meeting.domain.response.MeetingParticipantResponse;
import net.teumteum.meeting.domain.response.MeetingResponse;
import net.teumteum.meeting.domain.response.MeetingsResponse;
import net.teumteum.meeting.model.PageDto;
import net.teumteum.meeting.service.MeetingService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    private final SecurityService securityService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public MeetingResponse createMeeting(
        @RequestPart @Valid CreateMeetingRequest meetingRequest,
        @RequestPart List<MultipartFile> images) {
        Long userId = getCurrentUserId();
        return meetingService.createMeeting(images, meetingRequest, userId);
    }

    @GetMapping("/{meetingId}")
    @ResponseStatus(HttpStatus.OK)
    public MeetingResponse getMeetingById(@PathVariable("meetingId") Long meetingId) {
        Long userId = getCurrentUserId();
        return meetingService.getMeetingById(meetingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageDto<MeetingsResponse> getMeetingsByCondition(
        Pageable pageable,
        @RequestParam(value = "isOpen", required = false) Boolean isOpen,
        @RequestParam(value = "topic", required = false) Topic topic,
        @RequestParam(value = "meetingAreaStreet", required = false) String meetingAreaStreet,
        @RequestParam(value = "participantUserId", required = false) Long participantUserId,
        @RequestParam(value = "isBookmarked", required = false) Boolean isBookmarked,
        @RequestParam(value = "searchWord", required = false) String searchWord) {
        Long userId = getCurrentUserId();
        return meetingService.getMeetingsBySpecification(pageable, topic, meetingAreaStreet, participantUserId,
            searchWord, isBookmarked, isOpen, userId);
    }

    @PutMapping("/{meetingId}")
    @ResponseStatus(HttpStatus.OK)
    public MeetingResponse updateMeeting(@PathVariable Long meetingId,
        @RequestPart @Valid UpdateMeetingRequest request,
        @RequestPart List<MultipartFile> images) {
        Long userId = getCurrentUserId();
        return meetingService.updateMeeting(meetingId, images, request, userId);
    }

    @DeleteMapping("/{meetingId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMeeting(@PathVariable("meetingId") Long meetingId) {
        Long userId = getCurrentUserId();
        meetingService.deleteMeeting(meetingId, userId);
    }

    @PostMapping("/{meetingId}/participants")
    @ResponseStatus(HttpStatus.CREATED)
    public MeetingResponse addParticipant(@PathVariable("meetingId") Long meetingId) {
        Long userId = getCurrentUserId();
        return meetingService.addParticipant(meetingId, userId);
    }

    @DeleteMapping("/{meetingId}/participants")
    @ResponseStatus(HttpStatus.OK)
    public void deleteParticipant(@PathVariable("meetingId") Long meetingId) {
        Long userId = getCurrentUserId();
        meetingService.cancelParticipant(meetingId, userId);
    }

    @GetMapping("/{meetingId}/participants")
    @ResponseStatus(HttpStatus.OK)
    public List<MeetingParticipantResponse> getParticipants(@PathVariable("meetingId") Long meetingId) {
        Long userId = getCurrentUserId();
        return meetingService.getParticipants(meetingId, userId);
    }

    @PostMapping("/{meetingId}/reports")
    @ResponseStatus(HttpStatus.CREATED)
    public void reportMeeting(@PathVariable("meetingId") Long meetingId) {
        Long userId = getCurrentUserId();
        meetingService.reportMeeting(meetingId, userId);
    }

    @PostMapping("/{meetingId}/bookmarks")
    @ResponseStatus(HttpStatus.CREATED)
    public void addBookmark(@PathVariable("meetingId") Long meetingId) {
        Long userId = getCurrentUserId();
        meetingService.addBookmark(meetingId, userId);
    }

    @DeleteMapping("/{meetingId}/bookmarks")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBookmark(@PathVariable("meetingId") Long meetingId) {
        Long userId = getCurrentUserId();
        meetingService.cancelBookmark(meetingId, userId);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        Sentry.captureException(illegalArgumentException);
        return ErrorResponse.of(illegalArgumentException);
    }

    private Long getCurrentUserId() {
        return securityService.getCurrentUserId();
    }
}
