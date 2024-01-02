package net.teumteum.meeting.controller;

import lombok.RequiredArgsConstructor;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.meeting.domain.Meeting;
import net.teumteum.meeting.model.PageDto;
import net.teumteum.meeting.domain.Topic;
import net.teumteum.meeting.domain.response.MeetingResponse;
import net.teumteum.meeting.domain.response.MeetingsResponse;
import net.teumteum.meeting.service.MeetingService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meetings")
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping("/{meetingId}")
    @ResponseStatus(HttpStatus.OK)
    public MeetingResponse getMeetingById(@PathVariable("meetingId") Long meetingId) {
        return meetingService.getMeetingById(meetingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PageDto<MeetingsResponse> getMeetingsOrderByDate(Pageable pageable,
                                                            @RequestParam(value = "isOpen") boolean isOpen,
                                                            @RequestParam(value = "topic", required = false) Topic topic,
                                                            @RequestParam(value = "meetingAreaStreet", required = false) String meetingAreaStreet,
                                                            @RequestParam(value = "participantUserId", required = false) Long participantUserId,
                                                            @RequestParam(value = "searchWord", required = false) String searchWord) {

        Slice<Meeting> meetings = meetingService.getMeetingsBySpecification(pageable, topic, meetingAreaStreet, participantUserId, searchWord, isOpen);

        return PageDto.of(MeetingsResponse.of(meetings.getContent()), meetings.hasNext());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        return ErrorResponse.of(illegalArgumentException);
    }
}
