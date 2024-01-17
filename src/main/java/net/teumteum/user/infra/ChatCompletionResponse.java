package net.teumteum.user.infra;

import java.util.List;

public record ChatCompletionResponse(
    String id,
    String object,
    long created,
    String model,
    List<ChatCompletionResponse.Choice> choices,
    ChatCompletionResponse.Usage usage,
    String systemFingerprint
) {

    public record Choice(
        int index,
        Message message,
        Object logprobs,
        String finishReason
    ) {

    }

    public record Message(
        String role,
        String content
    ) {

    }

    public record Usage(
        int promptTokens,
        int completionTokens,
        int totalTokens
    ) {

    }
}
