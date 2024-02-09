package net.teumteum.meeting.domain;

import java.util.Set;

public record EndMeetingAlerted(
    Long meetingId,
    String meetingTitle,
    Set<Long> userIds
) {

}
