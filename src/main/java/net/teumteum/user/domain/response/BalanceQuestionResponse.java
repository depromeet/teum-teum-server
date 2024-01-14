package net.teumteum.user.domain.response;

import java.util.List;

public record BalanceQuestionResponse(
    String topic,
    List<String> balanceQuestion
) implements InterestQuestionResponse {

}
