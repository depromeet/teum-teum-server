package net.teumteum.user.domain.response;

import java.util.List;

public record InterestQuestionResponse(
    String topic,
    List<String> balanceQuestion
) {

}
