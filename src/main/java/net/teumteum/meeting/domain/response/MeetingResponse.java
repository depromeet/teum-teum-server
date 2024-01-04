package net.teumteum.meeting.domain.response;


import net.teumteum.meeting.domain.Meeting;
import net.teumteum.meeting.domain.Topic;

import java.time.LocalDateTime;
import java.util.Set;

public record MeetingResponse(
        Long id,
        Long hostId,
        Topic topic,
        String title,
        String introduction,
        Set<String> photoUrls,
        LocalDateTime promiseDateTime,
        int numberOfRecruits,
        MeetingArea meetingArea,
        Set<Long> participantIds
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
                meeting.getPromiseDateTime(),
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
}
