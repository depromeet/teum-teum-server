package net.teumteum.meeting.domain.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;
import net.teumteum.meeting.domain.Meeting;
import net.teumteum.meeting.domain.MeetingArea;
import net.teumteum.meeting.domain.Topic;

public record UpdateMeetingRequest(
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
    NewMeetingArea meetingArea
) {

    public static final Long IGNORE_ID = null;
    public static final Long IGNORE_HOST_ID = null;
    public static final Set<Long> IGNORE_PARTICIPANT_USER_IDS = null;
    public static final Set<String> IGNORE_IMAGE_URLS = null;

    public Meeting toMeeting() {
        return new Meeting(
            IGNORE_ID,
            title,
            IGNORE_HOST_ID,
            IGNORE_PARTICIPANT_USER_IDS,
            topic,
            introduction,
            NewMeetingArea.of(meetingArea),
            numberOfRecruits,
            promiseDateTime,
            IGNORE_IMAGE_URLS
        );
    }

    public record NewMeetingArea(
        @NotNull(message = "주소를 입력해주세요.")
        String address,
        @NotNull(message = "상세 주소를 입력해주세요.")
        String addressDetail
    ) {

        public static MeetingArea of(NewMeetingArea newMeetingArea) {
            return MeetingArea.of(
                newMeetingArea.address(),
                newMeetingArea.addressDetail()
            );
        }
    }
}
