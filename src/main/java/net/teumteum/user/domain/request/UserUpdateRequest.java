package net.teumteum.user.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Set;

import net.teumteum.user.domain.*;

import static net.teumteum.user.domain.RoleType.ROLE_USER;

public record UserUpdateRequest(
    Long id,
    String newName,
    String newBirth,
    Long newCharacterId,
    NewActivityArea newActivityArea,
    String newMbti,
    String newStatus,
    String newGoal,
    NewJob newJob,
    List<String> newInterests
) {

    private static final Long IGNORE_ID = null;
    private static final int IGNORE_MANNER_TEMPERATURE = -1;
    private static final OAuth IGNORE_O_AUTH = null;
    private static final boolean NOT_CERTIFICATED = false;
    private static final Terms IGNORE_TERMS = null;
    private static final Set<Long> IGNORE_FRIENDS = Set.of();

    public User toUser() {
        return new User(
            IGNORE_ID,
            newName,
            newBirth,
            newCharacterId,
            IGNORE_MANNER_TEMPERATURE,
                IGNORE_O_AUTH,
            ROLE_USER,
            new ActivityArea(
                newActivityArea.city,
                newActivityArea.streets
            ),
            newMbti,
            JobStatus.valueOf(newStatus),
            newGoal,
            new Job(
                newJob.name,
                NOT_CERTIFICATED,
                newJob.jobClass,
                newJob.detailClass
            ),
            newInterests,
            IGNORE_TERMS,
            IGNORE_FRIENDS
        );
    }

    public record NewActivityArea(
        String city,
        List<String> streets
    ) {

    }

    public record NewJob(
        String name,
        @JsonProperty("class")
        String jobClass,
        String detailClass
    ) {

    }
}
