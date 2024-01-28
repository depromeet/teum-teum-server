package net.teumteum.user.domain.request;

import static net.teumteum.user.domain.RoleType.ROLE_USER;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import net.teumteum.user.domain.Job;
import net.teumteum.user.domain.JobStatus;
import net.teumteum.user.domain.OAuth;
import net.teumteum.user.domain.Review;
import net.teumteum.user.domain.Terms;
import net.teumteum.user.domain.User;

public record UserUpdateRequest(
    @NotNull(message = "id 값은 필수 입력값입니다.")
    Long id,
    @NotBlank(message = "이름은 필수 입력값입니다.")
    String newName,
    @NotBlank(message = "생년월일은 필수 입력값입니다.")
    String newBirth,
    @NotNull(message = "캐릭터 아이디는 필수 입력값입니다.")
    Long newCharacterId,
    @NotBlank(message = "관심 지역은 필수 입력값입니다.")
    String newActivityArea,
    @NotBlank(message = "mbti 는 필수 입력값입니다.")
    String newMbti,
    @NotNull(message = "현재 상태는 필수 입력값입니다.")
    String newStatus,
    @Size(max = 50)
    @NotBlank(message = "목표는 필수 입력값입니다.")
    String newGoal,
    @Valid
    @NotNull(message = "직업 관련 값은 필수 입력값입니다.")
    NewJob newJob,
    @Size(max = 3, message = "관심 항목은 최대 3개까지 입력가능합니다.")
    @NotEmpty(message = "관심 항목은 최소 1개을 입력해야합니다.")
    List<String> newInterests
) {

    private static final Long IGNORE_ID = null;
    private static final int IGNORE_MANNER_TEMPERATURE = -1;
    private static final OAuth IGNORE_O_AUTH = null;
    private static final boolean NOT_CERTIFICATED = false;
    private static final Terms IGNORE_TERMS = null;
    private static final Set<Long> IGNORE_FRIENDS = Set.of();
    private static final List<Review> IGNORE_REVIEWS = List.of();

    public User toUser() {
        return new User(
            IGNORE_ID,
            newName,
            newBirth,
            newCharacterId,
            IGNORE_MANNER_TEMPERATURE,
            IGNORE_O_AUTH,
            ROLE_USER,
            newActivityArea,
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
            IGNORE_FRIENDS,
            IGNORE_REVIEWS
        );
    }

    public record NewJob(
        String name,
        @JsonProperty("class")
        @NotBlank(message = "직군은 필수 입력값입니다.")
        String jobClass,
        @NotBlank(message = "직무는 필수 입력값입니다.")
        String detailClass
    ) {

    }
}
