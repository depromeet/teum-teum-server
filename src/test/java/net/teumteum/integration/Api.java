package net.teumteum.integration;

import net.teumteum.meeting.config.PageableHandlerMethodArgumentResolver;
import net.teumteum.meeting.domain.Topic;
import net.teumteum.user.domain.request.UserUpdateRequest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

@WithMockUser(username = "user", roles = {"USER"})
@TestComponent
class Api {

    private final WebTestClient webTestClient;


    public Api(ApplicationContext applicationContext) {
        var controllers = applicationContext.getBeansWithAnnotation(Controller.class).values();
        webTestClient = WebTestClient.bindToController(controllers.toArray())
                .argumentResolvers(resolvers -> resolvers.addCustomResolver(new PageableHandlerMethodArgumentResolver()))
                .build();
    }


    ResponseSpec getUser(String token, Long userId) {
        return webTestClient
                .get()
                .uri("/users/" + userId)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange();
    }

    ResponseSpec getUsersById(String token, String userIds) {
        return webTestClient.get()
                .uri("/users?id=" + userIds)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchange();
    }

    ResponseSpec updateUser(String token, UserUpdateRequest userUpdateRequest) {
        return webTestClient
                .put()
                .uri("/users")
                .header(HttpHeaders.AUTHORIZATION, token)
                .bodyValue(userUpdateRequest)
                .exchange();
    }

    ResponseSpec addFriends(String token, Long friendId) {
        return webTestClient.post()
                .uri("/users/" + friendId + "/friends")
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
