package net.teumteum.user.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.teumteum.user.domain.InterestQuestion;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.response.InterestQuestionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class GptInterestQuestion implements InterestQuestion {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Value("${gpt.token}")
    private String gptToken;

    @Override
    public InterestQuestionResponse getQuestion(List<User> users) {
        var interests = parseInterests(users);
        var request = GptQuestionRequest.of(interests);

        return webClient.post()
            .bodyValue(request)
            .header(HttpHeaders.AUTHORIZATION, gptToken)
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(InterestQuestionResponse.class);
                }
                return response.createError();
            })
            .retry(5)
            .block(Duration.ofSeconds(5));
    }

    private String parseInterests(List<User> users) {
        var interests = new HashSet<String>();
        for (User user : users) {
            interests.addAll(user.getInterests().stream()
                .toList());
        }
        try {
            return objectMapper.writeValueAsString(interests);
        } catch (JsonProcessingException jsonProcessingException) {
            throw new IllegalStateException("관심사를 파싱하는 과정에서 에러가 발생했습니다.", jsonProcessingException);
        }
    }


    private record GptQuestionRequest(
        String model,
        List<Message> messages
    ) {

        private static final String LANGUAGE_MODEL = "gpt-3.5-turbo-1106";


        private static GptQuestionRequest of(String interests) {
            return new GptQuestionRequest(
                LANGUAGE_MODEL,
                List.of(Message.system(), Message.user(interests))
            );
        }

        private record Message(
            String role,
            String content
        ) {

            private static Message system() {
                return new Message(
                    "system",
                    "You are a chatbot that receives the user's interests and creates common topics of interest"
                        + " and balance games corresponding to the topics of interest in the form of sentences based on"
                        + " the interests. At this time, only two choices for the balance game must be given, and the"
                        + " choices must be separated by a comma The query results must be returned in JSON format"
                        + " according to the form below and other The JSON value must be answered in Korean without"
                        + " words. "
                        + "{\\\"topic\\\": Topic of common interest, \\\"balanceQuestion\\\": [Balance game options]}"
                );
            }

            private static Message user(String interests) {
                return new Message(
                    "user",
                    interests
                );
            }
        }
    }
}
