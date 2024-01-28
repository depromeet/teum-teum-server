package net.teumteum.user.domain.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import net.teumteum.user.domain.Review;

public record ReviewRegisterRequest(
    @Valid
    @Size(min = 3, max = 6)
    List<UserReviewRegisterRequest> reviews
) {

    public record UserReviewRegisterRequest(
        @NotNull(message = "유저의 id 는 필수 입력값입니다.")
        Long id,
        @NotNull(message = "리뷰는 필수 입력값입니다.")
        Review review
    ) {

    }
}
