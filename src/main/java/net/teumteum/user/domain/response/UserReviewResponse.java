package net.teumteum.user.domain.response;

import net.teumteum.user.domain.Review;

public record UserReviewResponse(
    Review review,
    long count
) {

}
