package net.teumteum.user.domain.request;

import static net.teumteum.user.domain.RoleType.ROLE_USER;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Set;
import net.teumteum.core.security.Authenticated;
import net.teumteum.user.domain.JobStatus;
import net.teumteum.user.domain.OAuth;
import net.teumteum.user.domain.Terms;
import net.teumteum.user.domain.User;

public record UserRegisterRequest(
    String id,
    String name,
    String birth,
    Long characterId,
    Authenticated authenticated,
    ActivityArea activityArea,
    String mbti,
    String status,
    Job job,
    List<String> interests,
    String goal
) {

    private static final Long IGNORE_ID = null;
    private static final int IGNORE_MANNER_TEMPERATURE = -1;
    private static final boolean NOT_CERTIFICATED = false;
    private static final Terms IGNORE_TERMS = null;
    private static final Set<Long> IGNORE_FRIENDS = Set.of();


    public User toUser() {
        return new User(
            IGNORE_ID,
            name,
            birth,
            characterId,
            IGNORE_MANNER_TEMPERATURE,
            new OAuth(
                id,
                authenticated
            ),
            ROLE_USER,
            new net.teumteum.user.domain.ActivityArea(
                activityArea.city,
                activityArea.street
            ),
            mbti,
            JobStatus.valueOf(status),
            goal,
            new net.teumteum.user.domain.Job(
                job.name,
                NOT_CERTIFICATED,
                job.jobClass,
                job.detailClass
            ),
            interests,
            IGNORE_TERMS,
            IGNORE_FRIENDS
        );
    }

    public record ActivityArea(
        String city,
        List<String> street
    ) {

    }

    public record Job(
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String name,
        String jobClass,
        String detailClass
    ) {

    }
}
