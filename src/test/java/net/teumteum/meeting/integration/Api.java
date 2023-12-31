package net.teumteum.meeting.integration;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

@TestComponent
public class Api {

    private final WebTestClient webTestClient;

    public Api(ApplicationContext applicationContext) {
        var controllers = applicationContext.getBeansWithAnnotation(Controller.class).values();
        webTestClient = WebTestClient.bindToController(controllers.toArray()).build();
    }

    ResponseSpec getMeetingById(String token, Long meetingId) {
        return webTestClient.get()
                .uri("/meetings/" + meetingId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange();
    }

    ResponseSpec getOpenMeetings(String token, Long cursorId, int size) {
        return webTestClient.get()
                .uri("/meetings" +
                        "?cursorId=" + cursorId +
                        "&size=" + size)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange();
    }
}
