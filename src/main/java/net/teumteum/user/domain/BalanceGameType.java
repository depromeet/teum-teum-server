package net.teumteum.user.domain;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import net.teumteum.user.domain.response.InterestQuestionResponse;

public enum BalanceGameType {

    BALANCE("balance", (users, interestQuestion) -> interestQuestion.getBalanceGame(users)),
    STORY("story", (users, interestQuestion) -> interestQuestion.getStoryGame(users)),
    ;

    private final String value;
    private final BiFunction<List<User>, InterestQuestion, InterestQuestionResponse> behavior;

    BalanceGameType(String value, BiFunction<List<User>, InterestQuestion, InterestQuestionResponse> behavior) {
        this.value = value;
        this.behavior = behavior;
    }

    public static BalanceGameType of(String value) {
        return Arrays.stream(BalanceGameType.values())
            .filter(type -> type.value.equals(value))
            .findAny()
            .orElseThrow(
                () -> new IllegalArgumentException("\"" + value + "\" 에 해당하는 enum값을 찾을 수 없습니다.")
            );
    }

    public InterestQuestionResponse getInterestQuestionResponse(List<User> users, InterestQuestion interestQuestion) {
        return behavior.apply(users, interestQuestion);
    }
}
