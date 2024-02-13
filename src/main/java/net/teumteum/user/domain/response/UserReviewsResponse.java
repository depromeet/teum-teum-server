package net.teumteum.user.domain.response;

import java.util.List;

public record UserReviewsResponse(
    List<UserReviewResponse> reviews
) {

    public static UserReviewsResponse of(List<UserReviewResponse> reviews) {
        return new UserReviewsResponse(reviews);
    }
}
