package net.teumteum.meeting.domain;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MeetingFixture {

    public static Meeting getDefaultMeeting() {
        return newMeetingByBuilder(MeetingBuilder.builder().build());
    }

    public static Meeting getOpenMeeting() {
        return newMeetingByBuilder(MeetingBuilder.builder()
                .promiseDateTime(LocalDateTime.of(4000, 1, 1, 0, 0))
                .build()
        );
    }

    public static Meeting getCloseMeeting() {
        return newMeetingByBuilder(MeetingBuilder.builder()
                .promiseDateTime(LocalDateTime.of(2000, 1, 1, 0, 0))
                .build()
        );
    }

    public static Meeting getOpenMeetingWithTopic(Topic topic) {
        return newMeetingByBuilder(MeetingBuilder.builder()
                .promiseDateTime(LocalDateTime.of(4000, 1, 1, 0, 0))
                .topic(topic)
                .build()
        );
    }

    public static Meeting getCloseMeetingWithTopic(Topic topic) {
        return newMeetingByBuilder(MeetingBuilder.builder()
                .promiseDateTime(LocalDateTime.of(2000, 1, 1, 0, 0))
                .topic(topic)
                .build()
        );
    }

    public static Meeting getOpenMeetingWithStreet(String street) {
        return newMeetingByBuilder(MeetingBuilder.builder()
                .promiseDateTime(LocalDateTime.of(4000, 1, 1, 0, 0))
                .meetingArea(new MeetingArea("서울특별시", street, "강남대로 390"))
                .build()
        );
    }

    public static Meeting getOpenMeetingWithParticipantUserId(Long participantUserId) {
        return newMeetingByBuilder(MeetingBuilder.builder()
                .promiseDateTime(LocalDateTime.of(4000, 1, 1, 0, 0))
                .participantUserIds(new ArrayList<>(List.of(participantUserId)))
                .build()
        );
    }

    public static Meeting getCloseMeetingWithParticipantUserId(Long participantUserId) {
        return newMeetingByBuilder(MeetingBuilder.builder()
                .promiseDateTime(LocalDateTime.of(2000, 1, 1, 0, 0))
                .participantUserIds(new ArrayList<>(List.of(participantUserId)))
                .build()
        );
    }

    public static Meeting getOpenMeetingWithTitle(String title) {
        return newMeetingByBuilder(MeetingBuilder.builder()
                .promiseDateTime(LocalDateTime.of(4000, 1, 1, 0, 0))
                .title(title)
                .build()
        );
    }

    public static Meeting getCloseMeetingWithTitle(String title) {
        return newMeetingByBuilder(MeetingBuilder.builder()
                .title(title)
                .promiseDateTime(LocalDateTime.of(2000, 1, 1, 0, 0))
                .build()
        );
    }

    public static Meeting getOpenMeetingWithIntroduction(String introduction) {
        return newMeetingByBuilder(MeetingBuilder.builder()
                .promiseDateTime(LocalDateTime.of(4000, 1, 1, 0, 0))
                .introduction(introduction)
                .build()
        );
    }

    public static Meeting getCloseMeetingWithIntroduction(String introduction) {
        return newMeetingByBuilder(MeetingBuilder.builder()
                .promiseDateTime(LocalDateTime.of(2000, 1, 1, 0, 0))
                .introduction(introduction)
                .build()
        );
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
        private Long id = null;

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
        private LocalDateTime promiseDateTime = LocalDateTime.of(2024, 10, 10, 0, 0);

        @Builder.Default
        private List<String> imageUrls = new ArrayList<>(List.of("https://www.google.com"));
    }

}
