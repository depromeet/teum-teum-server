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
    Set<Long> participantIds,
    Boolean isBookmarked
) {

    public static MeetingResponse of(
        Meeting meeting,
        Boolean isBookmarked
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
            meeting.getParticipantUserIds(),
            isBookmarked
        );
    }

    public record MeetingArea(
        String mainStreet,
        String address,
        String addressDetail
    ) {

        public static MeetingArea of(
            Meeting meeting
        ) {
            return new MeetingArea(
                meeting.getMeetingArea().getMainStreet(),
                meeting.getMeetingArea().getAddress(),
                meeting.getMeetingArea().getAddressDetail()
            );
        }
    }
}
