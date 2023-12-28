package net.teumteum.meeting.domain;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MeetingFixture {

    public static Meeting getMeetingWithId(Long id) {
        return newMeetingByBuilder(MeetingBuilder.builder()
                .id(id)
                .build());
    }

    public static Meeting getDefaultMeeting() {
        return newMeetingByBuilder(MeetingBuilder.builder().build());
    }

    public static Meeting newMeetingByBuilder(MeetingBuilder meetingBuilder) {
        return new Meeting(
                meetingBuilder.id,
                meetingBuilder.title,
                meetingBuilder.hostUserId,
                meetingBuilder.participantUserIds,
                meetingBuilder.topic,
                meetingBuilder.introduction,
                meetingBuilder.meetingArea,
                meetingBuilder.numberOfRecruits,
                meetingBuilder.promiseDateTime,
                meetingBuilder.imageUrls
        );
    }

    @Builder
    public static class MeetingBuilder {
        @Builder.Default
        private Long id = 0L;

        @Builder.Default
        private String title = "모임 제목";

        @Builder.Default
        private Long hostUserId = 0L;

        @Builder.Default
        private List<Long> participantUserIds = new ArrayList<>(List.of(0L));

        @Builder.Default
        private Topic topic = Topic.스터디;

        @Builder.Default
        private String introduction = "모임에 대한 간단한 설명입니다.";

        @Builder.Default
        private MeetingArea meetingArea = new MeetingArea("서울특별시", "강남구", "강남대로 390");

        @Builder.Default
        private int numberOfRecruits = 3;

        @Builder.Default
        private PromiseDateTime promiseDateTime = new PromiseDateTime(LocalDate.of(2023, 1, 1), LocalTime.of(12, 0));

        @Builder.Default
        private List<String> imageUrls = new ArrayList<>(List.of("https://www.google.com"));
    }

}
