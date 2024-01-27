package net.teumteum.user.domain.response;

import net.teumteum.user.domain.Review;

public record UserReviewsResponse(
    Review review,
    long count
) {

}
