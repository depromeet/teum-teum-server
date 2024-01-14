package net.teumteum.user.domain;

import java.util.List;
import net.teumteum.user.domain.response.BalanceQuestionResponse;
import net.teumteum.user.domain.response.StoryQuestionResponse;

public interface InterestQuestion {

    BalanceQuestionResponse getBalanceGame(List<User> users);

    StoryQuestionResponse getStoryGame(List<User> users);

}
