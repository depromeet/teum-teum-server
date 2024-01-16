package net.teumteum.user.domain.request;

import static net.teumteum.user.domain.RoleType.ROLE_USER;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import net.teumteum.core.security.Authenticated;
import net.teumteum.user.domain.JobStatus;
import net.teumteum.user.domain.OAuth;
import net.teumteum.user.domain.User;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record UserRegisterRequest(
    @NotBlank(message = "id 값은 필수 입력값입니다.")
    String id,
    @NotNull(message = "동의 항목은 필수 입력값입니다.")
    Terms terms,
    @NotBlank(message = "이름은 필수 입력값입니다.")
    String name,
    @NotBlank(message = "생년월일은 필수 입력값입니다.")
    String birth,
    @NotNull(message = "캐릭터 아이디는 필수 입력값입니다.")
    Long characterId,
    @NotNull(message = "소셜 로그인 타입은 필수 입력값입니다.")
    Authenticated authenticated,
    @NotBlank(message = "관심 지역은 필수 입력값입니다.")
    String activityArea,
    @NotBlank(message = "mbti 는 필수 입력값입니다.")
    String mbti,
    @NotNull(message = "현재 상태는 필수 입력값입니다.")
    String status,
    @NotNull(message = "직업 관련 값은 필수 입력값입니다.")
    Job job,
    @Size(max = 3, message = "관심 항목은 최대 3개까지 입력가능합니다.")
    @NotEmpty(message = "관심 항목은 최소 1개을 입력해야합니다.")
    List<String> interests,
    @Size(max = 50)
    @NotBlank(message = "목표는 필수 입력값입니다.")
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
            activityArea,
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

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public record Terms(
        Boolean service,
        Boolean privatePolicy
    ) {

    }


    public record Job(
        String name,
        @JsonProperty("class")
        @NotBlank(message = "직군은 필수 입력값입니다.")
        String jobClass,
        @NotBlank(message = "직무는 필수 입력값입니다.")
        String detailClass
    ) {

    }
}
