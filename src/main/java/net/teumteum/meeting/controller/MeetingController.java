package net.teumteum.meeting.controller;

import lombok.RequiredArgsConstructor;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.meeting.domain.response.MeetingResponse;
import net.teumteum.meeting.service.MeetingService;
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        return ErrorResponse.of(illegalArgumentException);
    }
}
