package net.teumteum.user.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import net.teumteum.user.domain.User;

public record FriendsResponse(
    List<Friend> friends
) {

    public static FriendsResponse of(List<User> users) {
        return new FriendsResponse(
            users.stream()
                .map(Friend::of)
                .toList()
        );
    }

    public record Friend(
        Long id,
        Long characterId,
        String name,
        Job job
    ) {

        public static Friend of(User user) {
            return new Friend(
                user.getId(),
                user.getCharacterId(),
                user.getName(),
                Job.of(user)
            );
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

}
