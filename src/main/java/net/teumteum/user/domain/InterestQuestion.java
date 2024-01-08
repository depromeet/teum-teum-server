package net.teumteum.user.domain;

import java.util.List;
import net.teumteum.user.domain.response.InterestQuestionResponse;

@FunctionalInterface
public interface InterestQuestion {

    InterestQuestionResponse getQuestion(List<User> users);

}
