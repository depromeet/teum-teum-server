package net.teumteum.meeting.controller;

import lombok.RequiredArgsConstructor;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.meeting.domain.ResultCursor;
import net.teumteum.meeting.domain.response.MeetingResponse;
import net.teumteum.meeting.service.MeetingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * @param cursorId 커서로 사용할 모임의 id
     * @param size     조회할 모임의 개수
     * @return size 개수의 MeetingResponse 의 데이터, hasNext, cursorId
     * @apiNote 커서 방식으로 구현한 모임 조회 API 입니다. 첫 조회에는 디폴트값 0을 입력받습니다. size + 1 개수만큼 조회하여 이후 조회를 위한 커서를 cursorId 로 응답합니다.
     * 클라이언트는 hasNext 가 true 일 경우 cursorId 를 다음 조회에 사용합니다.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResultCursor<MeetingResponse> getMeetingsOrderByDate(@RequestParam(value = "cursorId", defaultValue = "0") Long cursorId,
                                                                @RequestParam(value = "size", defaultValue = "20") int size) {
        List<MeetingResponse> meetings = meetingService.getMeetings(cursorId, PageRequest.of(0, size + 1));

        if (hasNextData(size, meetings)) {
            return ResultCursor.of(meetings.subList(0, size), true, meetings.get(size).id());
        }
        return ResultCursor.of(meetings, false, null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        return ErrorResponse.of(illegalArgumentException);
    }

    private boolean hasNextData(int size, List<MeetingResponse> meetings) {
        return meetings.size() == size + 1;
    }

}
