package net.teumteum.meeting.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;

public class MeetingFixture {

    public static Meeting getDefaultMeeting() {
        return newMeetingByBuilder(MeetingBuilder.builder().build());
    }

    public static Meeting getMeetingWithPromiseDate(LocalDateTime promiseDate) {
        return newMeetingByBuilder(MeetingBuilder.builder()
            .promiseDateTime(promiseDate)
            .build()
        );
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

    public static Meeting getOpenFullMeeting() {
        return newMeetingByBuilder(MeetingBuilder.builder()
            .promiseDateTime(LocalDateTime.of(4000, 1, 1, 0, 0))
            .numberOfRecruits(3)
            .participantUserIds(new HashSet<>(List.of(0L, 1L, 2L)))
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

    public static Meeting getOpenMeetingWithMainStreet(String mainStreet) {
        return newMeetingByBuilder(MeetingBuilder.builder()
            .promiseDateTime(LocalDateTime.of(4000, 1, 1, 0, 0))
            .meetingArea(new MeetingArea(mainStreet, "서울특별시", "강남대로 390"))
            .build()
        );
    }

    public static Meeting getOpenMeetingWithParticipantUserId(Long participantUserId) {
        return newMeetingByBuilder(MeetingBuilder.builder()
            .promiseDateTime(LocalDateTime.of(4000, 1, 1, 0, 0))
            .participantUserIds(new HashSet<>(List.of(participantUserId)))
            .build()
        );
    }

    public static Meeting getCloseMeetingWithParticipantUserId(Long participantUserId) {
        return newMeetingByBuilder(MeetingBuilder.builder()
            .promiseDateTime(LocalDateTime.of(2000, 1, 1, 0, 0))
            .participantUserIds(new HashSet<>(List.of(participantUserId)))
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

    public static Meeting getOpenMeetingWithHostId(Long hostId) {
        return newMeetingByBuilder(MeetingBuilder.builder()
            .promiseDateTime(LocalDateTime.of(4000, 1, 1, 0, 0))
            .hostUserId(hostId)
            .build()
        );
    }

    public static Meeting getCloseMeetingWithHostId(Long hostId) {
        return newMeetingByBuilder(MeetingBuilder.builder()
            .promiseDateTime(LocalDateTime.of(2000, 1, 1, 0, 0))
            .hostUserId(hostId)
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
        private Set<Long> participantUserIds = new HashSet<>(List.of(0L));

        @Builder.Default
        private Topic topic = Topic.스터디;

        @Builder.Default
        private String introduction = "모임에 대한 간단한 설명입니다.";

        @Builder.Default
        private MeetingArea meetingArea = MeetingArea.of("서울 강남구 강남대로 390", "강남역 11번 출구");

        @Builder.Default
        private int numberOfRecruits = 3;

        @Builder.Default
        private LocalDateTime promiseDateTime = LocalDateTime.of(2024, 10, 10, 0, 0);

        @Builder.Default
        private Set<String> imageUrls = new HashSet<>(List.of("/1/image.jpg", "/2/image.jpg"));
    }

}
