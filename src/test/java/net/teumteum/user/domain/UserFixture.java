package net.teumteum.user.domain;

import static net.teumteum.core.security.Authenticated.네이버;
import static net.teumteum.user.domain.Review.별로에요;
import static net.teumteum.user.domain.Review.좋아요;
import static net.teumteum.user.domain.Review.최고에요;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;

public class UserFixture {

    public static User getNullIdUser() {
        return newUserByBuilder(UserBuilder.builder()
            .id(null)
            .build());
    }

    public static User getIdUser() {
        return newUserByBuilder(UserBuilder.builder()
            .id(1L)
            .build());
    }

    public static User getUserWithId(Long id) {
        return newUserByBuilder(UserBuilder.builder()
            .id(id)
            .build());
    }

    public static User getDefaultUser() {
        return newUserByBuilder(UserBuilder.builder().build());
    }

    public static User newUserByBuilder(UserBuilder userBuilder) {
        return new User(
            userBuilder.id,
            userBuilder.name,
            userBuilder.birth,
            userBuilder.characterId,
            userBuilder.mannerTemperature,
            userBuilder.oauth,
            userBuilder.roleType,
            userBuilder.activityArea,
            userBuilder.mbti,
            userBuilder.status,
            userBuilder.goal,
            userBuilder.job,
            userBuilder.interests,
            userBuilder.terms,
            Set.of(),
            userBuilder.reviews
        );
    }

    @Builder
    public static class UserBuilder {

        @Builder.Default
        private Long id = 0L;
        @Builder.Default
        private String name = "Jennifer";
        @Builder.Default
        private String birth = "20000205";
        @Builder.Default
        private Long characterId = 1L;
        @Builder.Default
        private int mannerTemperature = 36;
        @Builder.Default
        private OAuth oauth = new OAuth(UUID.randomUUID().toString(), 네이버);
        @Builder.Default
        private RoleType roleType = RoleType.ROLE_USER;
        @Builder.Default
        private String activityArea = "서울 강남";
        @Builder.Default
        private String mbti = "ESFP";
        @Builder.Default
        private JobStatus status = JobStatus.취업준비생;
        @Builder.Default
        private String goal = "취업하기";
        @Builder.Default
        private Job job = new Job("netflix", true, "developer", "backend");
        @Builder.Default
        private List<String> interests = List.of(
            "game", "sleep", "Eating delicious food"
        );
        @Builder.Default
        private Terms terms = new Terms(true, true);
        @Builder.Default
        private List<Review> reviews = new ArrayList<>(Arrays.asList(최고에요, 최고에요, 최고에요, 별로에요, 좋아요, 좋아요));
    }

}
