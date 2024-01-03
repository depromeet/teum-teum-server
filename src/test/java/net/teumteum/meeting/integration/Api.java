package net.teumteum.meeting.integration;

import net.teumteum.meeting.config.PageableHandlerMethodArgumentResolver;
import net.teumteum.meeting.domain.Topic;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

@TestComponent
public class Api {

    private final WebTestClient webTestClient;

    public Api(ApplicationContext applicationContext) {
        var controllers = applicationContext.getBeansWithAnnotation(Controller.class).values();
        webTestClient = WebTestClient.bindToController(controllers.toArray())
                .argumentResolvers(resolvers -> resolvers.addCustomResolver(new PageableHandlerMethodArgumentResolver()))
                .build();
    }

    ResponseSpec getMeetingById(String token, Long meetingId) {
        return webTestClient.get()
                .uri("/meetings/" + meetingId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange();
    }

    ResponseSpec getMeetingsByTopic(String token, Pageable pageable, boolean isOpen, Topic topic) {
        String sort = pageable.getSort().toString().replace(": ", ",");
        String uri = "/meetings?sort=" + sort +
                "&page=" + pageable.getOffset() +
                "&size=" + pageable.getPageSize() +
                "&isOpen=" + isOpen +
                "&topic=" + topic;

        return webTestClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange();
    }
}
