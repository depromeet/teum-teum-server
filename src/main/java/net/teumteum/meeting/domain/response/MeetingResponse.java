package net.teumteum.meeting.domain.response;


import net.teumteum.meeting.domain.Meeting;
import net.teumteum.meeting.domain.Topic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record MeetingResponse(
        Long id,
        Long hostId,
        Topic topic,
        String title,
        String introduction,
        List<String> photoUrls,
        PromiseDateTime promiseDateTime,
        int numberOfRecruits,
        MeetingArea meetingArea,
        List<Long> participantIds
) {
    public static MeetingResponse of(
            Meeting meeting
    ) {
        return new MeetingResponse(
                meeting.getId(),
                meeting.getHostUserId(),
                meeting.getTopic(),
                meeting.getTitle(),
                meeting.getIntroduction(),
                meeting.getImageUrls(),
                PromiseDateTime.of(meeting),
                meeting.getNumberOfRecruits(),
                MeetingArea.of(meeting),
                meeting.getParticipantUserIds()
        );
    }

    public record MeetingArea(
            String city,
            String street,
            String zipCode
    ) {
        public static MeetingArea of(
                Meeting meeting
        ) {
            return new MeetingArea(
                    meeting.getMeetingArea().getCity(),
                    meeting.getMeetingArea().getStreet(),
                    meeting.getMeetingArea().getZipCode()
            );
        }
    }

    public record PromiseDateTime(
            LocalDate date,
            LocalTime time
    ) {
        public static PromiseDateTime of(
                Meeting meeting
        ) {
            return new PromiseDateTime(
                    meeting.getPromiseDateTime().getDate(),
                    meeting.getPromiseDateTime().getTime()
            );
        }
    }
}
