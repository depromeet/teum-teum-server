package net.teumteum.meeting.domain.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import net.teumteum.meeting.domain.Topic;

import java.time.LocalDateTime;

public record CreateMeetingRequest(
    @NotNull(message = "모임 주제를 입력해주세요.")
    Topic topic,
    @NotNull(message = "모임 제목을 입력해주세요.")
    @Size(min = 2, max = 32, message = "모임 제목은 2자 이상 32자 이하로 입력해주세요.")
    String title,
    @NotNull(message = "모임 소개를 입력해주세요.")
    @Size(min = 10, max = 200, message = "모임 소개는 10자 이상 200자 이하로 입력해주세요.")
    String introduction,
    @NotNull(message = "약속 시간을 입력해주세요.")
    @Future(message = "약속 시간은 현재 시간보다 미래여야 합니다.")
    LocalDateTime promiseDateTime,
    @NotNull(message = "모집 인원을 입력해주세요.")
    int numberOfRecruits,
    @Valid
    MeetingArea meetingArea
) {

    public record MeetingArea(
        @NotNull(message = "주소를 입력해주세요.")
        String address,
        @NotNull(message = "상세 주소를 입력해주세요.")
        String addressDetail
    ) {

    }
}
