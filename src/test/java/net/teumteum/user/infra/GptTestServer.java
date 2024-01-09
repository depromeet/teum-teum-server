package net.teumteum.user.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.io.IOException;
import java.nio.charset.Charset;
import net.teumteum.user.domain.response.InterestQuestionResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

public class GptTestServer {

    private final MockWebServer mockWebServer = new MockWebServer();
    private final ObjectMapper objectMapper = objectMapper();

    {
        try {
            mockWebServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void enqueue(InterestQuestionResponse interestQuestionResponse) {
        mockWebServer.enqueue(
            new MockResponse().setBody(toBuffer(interestQuestionResponse))
                .setResponseCode(200)
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        );
    }

    private Buffer toBuffer(InterestQuestionResponse interestQuestionResponse) {
        try (var buffer = new Buffer()) {
            return buffer.writeString(objectMapper.writeValueAsString(interestQuestionResponse),
                Charset.defaultCharset());
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    public void enqueue400() {
        mockWebServer.enqueue(
            new MockResponse().setResponseCode(400)
        );
    }

    @Bean
    private WebClient testGptWebClient(GptTestServer gptTestServer) {
        return WebClient.create(gptTestServer.mockWebServer.url("").toString());
    }

    @Bean
    private ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();
        return objectMapper.registerModule(new ParameterNamesModule());
    }
}
