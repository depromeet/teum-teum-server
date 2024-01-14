package net.teumteum.user.domain.request;

import static net.teumteum.user.domain.RoleType.ROLE_USER;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import net.teumteum.core.security.Authenticated;
import net.teumteum.user.domain.JobStatus;
import net.teumteum.user.domain.OAuth;
import net.teumteum.user.domain.User;

public record UserRegisterRequest(
    String id,
    Terms terms,
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

    public User toUser() {
        return new User(
            null,
            name,
            birth,
            characterId,
            0,
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
                false,
                job.jobClass,
                job.detailClass
            ),
            interests,
            new net.teumteum.user.domain.Terms(
                terms.service,
                terms.privatePolicy
            ),
            null
        );
    }

    public record Terms(
        boolean service,
        boolean privatePolicy
    ) {

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
