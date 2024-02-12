package net.teumteum.meeting.domain.response;

import net.teumteum.user.domain.User;

public record MeetingParticipantsResponse(
    Long id,
    Long characterId,
    String name,
    String job
) {

    public static MeetingParticipantsResponse of(
        User user
    ) {
        return new MeetingParticipantsResponse(
            user.getId(),
            user.getCharacterId(),
            user.getName(),
            user.getJob().getDetailJobClass()
        );
    }

}
