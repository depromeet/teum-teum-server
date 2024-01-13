package net.teumteum.user.domain.response;

public record StoryQuestionResponse(
    String topic,
    String story
) implements InterestQuestionResponse {

}
