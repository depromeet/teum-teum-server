package net.teumteum.user.domain;

import java.util.List;
import lombok.Builder;

public class UserFixture {

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
            userBuilder.activityArea,
            userBuilder.mbti,
            userBuilder.status,
            userBuilder.goal,
            userBuilder.job,
            userBuilder.interests,
            userBuilder.terms
        );
    }

    @Builder
    public static class UserBuilder {

        @Builder.Default
        private Long id = 0L;
        @Builder.Default
        private String name = "Jennifer";
        @Builder.Default
        private String birth = "2000.02.05";
        @Builder.Default
        private Long characterId = 1L;
        @Builder.Default
        private int mannerTemperature = 36;
        @Builder.Default
        private Oauth oauth = new Oauth("hello123@naver.com", "naver");
        @Builder.Default
        private ActivityArea activityArea = new ActivityArea("서울", List.of("강남", "홍대"));
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
    }

}