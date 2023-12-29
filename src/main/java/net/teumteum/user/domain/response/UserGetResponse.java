package net.teumteum.user.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import net.teumteum.user.domain.User;

public record UserGetResponse(
    Long id,
    String name,
    String birth,
    Long characterId,
    int mannerTemperature,
    String authenticated,
    ActivityArea activityArea,
    String mbti,
    String status,
    String goal,
    Job job,
    List<String> interests
) {

    public static UserGetResponse of(User user) {
        return new UserGetResponse(
            user.getId(),
            user.getName(),
            user.getBirth(),
            user.getCharacterId(),
            user.getMannerTemperature(),
            user.getOauth().getAuthenticated(),
            ActivityArea.of(user),
            user.getMbti(),
            user.getStatus().name(),
            user.getGoal(),
            Job.of(user),
            user.getInterests()
        );
    }

    public record ActivityArea(
        String city,
        List<String> streets
    ) {

        public static ActivityArea of(User user) {
            return new ActivityArea(
                user.getActivityArea().getCity(),
                user.getActivityArea().getStreet()
            );
        }

    }

    public record Job(
        String name,
        boolean certificated,
        @JsonProperty("class")
        String jobClass,
        String detailClass
    ) {

        public static Job of(User user) {
            return new Job(
                user.getJob().getName(),
                user.getJob().isCertificated(),
                user.getJob().getJobClass(),
                user.getJob().getDetailJobClass()
            );
        }
    }
}
