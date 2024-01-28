package net.teumteum.user.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import net.teumteum.user.domain.InterestQuestion;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.response.BalanceQuestionResponse;
import net.teumteum.user.domain.response.StoryQuestionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class GptInterestQuestion implements InterestQuestion {

    private static final int MAX_RETRY_COUNT = 5;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Value("${gpt.token}")
    private String gptToken;


    @Override
    public BalanceQuestionResponse getBalanceGame(List<User> users) {
        var interests = parseInterests(users);
        var request = GptQuestionRequest.balanceGame(interests);

        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + gptToken)
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(ChatCompletionResponse.class);
                }
                return response.createError();
            })
            .map(response -> {
                try {
                    return objectMapper.readValue(response.choices().get(0).message().content(),
                        BalanceQuestionResponse.class);
                } catch (JsonProcessingException e) {
                    throw new IllegalStateException(e);
                }
            })
            .retry(MAX_RETRY_COUNT)
            .subscribeOn(Schedulers.fromExecutor(executorService))
            .block(Duration.ofSeconds(20));
    }

    @Override
    public StoryQuestionResponse getStoryGame(List<User> users) {
        var interests = parseInterests(users);
        var request = GptQuestionRequest.story(interests);

        return webClient.post()
            .uri("/v1/chat/completions")
            .bodyValue(request)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + gptToken)
            .exchangeToMono(response -> {
                if (response.statusCode().is2xxSuccessful()) {
                    return response.bodyToMono(ChatCompletionResponse.class);
                }
                return response.createError();
            })
            .map(response -> {
                try {
                    return objectMapper.readValue(response.choices().get(0).message().content(),
                        StoryQuestionResponse.class);
                } catch (JsonProcessingException e) {
                    throw new IllegalStateException(e);
                }
            })
            .retry(MAX_RETRY_COUNT)
            .subscribeOn(Schedulers.fromExecutor(executorService))
            .block(Duration.ofSeconds(20));
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


        private static GptQuestionRequest balanceGame(String interests) {
            return new GptQuestionRequest(LANGUAGE_MODEL, List.of(Message.balanceGame(), Message.user(interests)));
        }

        private static GptQuestionRequest story(String interests) {
            return new GptQuestionRequest(LANGUAGE_MODEL, List.of(Message.story(), Message.user(interests)));
        }

        private record Message(String role, String content) {

            private static Message balanceGame() {
                return new Message("system",
                    "당신은 사용자의 관심사들을 입력받아 관심사 게임을 응답하는 챗봇입니다. 입력된 관심사중 하나를 랜덤으로 선택해서 관심사 게임을 만들어주세요. 관심사 게임은 \"공통 관심 주제\"와 \"밸런스 게임의 질문 선택지\" 로 이루어져 있습니다. \"밸런스 게임의 질문 선택지\"는 문장형태로 이루어지며 각각 하나의 질문으로 무조건 2개 응답되어야 합니다. 이때, \"밸런스 게임의 질문 선택지\"는 서로 완전히 반대되어야 하며 각각 36자 이하에 존댓말로 생성되어야 합니다. 응답은 다음 JSON 형태로 응답해주세요. {\"topic\": 공통 관심 주제, \"balanceQuestion\": [밸런스 게임의 질문 선택지 2개]} 이때, 부가적인 설명없이 JSON만 응답해야하며, JSON의 VALUE는 모두 한국어로 응답해주세요.");
            }

            private static Message story() {
                return new Message("system",
                    "당신은 사용자의 관심사들을 입력받아 관심사 게임을 응답하는 챗봇입니다. 관심사 게임은 \"공통 관심 주제\"와 \"관심 주제와 연관되는 질문\" 로 이루어져 있습니다.이때 \"관심 주제와 연관되는 질문\" 은 최대 76자로 제한합니다. 응답은 다음 JSON 형태로 형태로 응답해주세요. {\"topic\": 공통 관심 주제, \"story\": 관심 주제와 연관되는 질문} 이때, 부가적인 설명없이 JSON만 응답해야하며, JSON의 VALUE는 모두 한국어로 응답해주세요.");
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
