package net.teumteum.meeting.domain.response;

import java.util.List;
import net.teumteum.user.domain.User;

public record MeetingParticipantsForReviewResponse(
    List<MeetingParticipantForReviewResponse> participants
) {

    public static MeetingParticipantsForReviewResponse of(List<User> users) {
        return new MeetingParticipantsForReviewResponse(
            users.stream()
                .map(user -> new MeetingParticipantForReviewResponse(
                    user.getId(),
                    user.getCharacterId(),
                    user.getName(),
                    user.getJob().getDetailJobClass()))
                .toList()
        );
    }

    public record MeetingParticipantForReviewResponse(
        Long id,
        Long characterId,
        String name,
        String job
    ) {

    }
}
