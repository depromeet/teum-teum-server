package net.teumteum.meeting.domain.response;

import net.teumteum.user.domain.User;

public record MeetingParticipantResponse(
    Long id,
    Long characterId,
    String name,
    String job
) {

    public static MeetingParticipantResponse of(
        User user
    ) {
        return new MeetingParticipantResponse(
            user.getId(),
            user.getCharacterId(),
            user.getName(),
            user.getJob().getDetailJobClass()
        );
    }

}
