package net.teumteum.integration;

import java.util.List;
import net.teumteum.meeting.config.PageableHandlerMethodArgumentResolver;
import net.teumteum.meeting.domain.Topic;
import net.teumteum.teum_teum.domain.request.UserLocationRequest;
import net.teumteum.user.domain.request.ReviewRegisterRequest;
import net.teumteum.user.domain.request.UserRegisterRequest;
import net.teumteum.user.domain.request.UserUpdateRequest;
import net.teumteum.user.domain.request.UserWithdrawRequest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

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

    ResponseSpec getMe(String token) {
        return webTestClient
            .get()
            .uri("/users/me")
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

    ResponseSpec getFriendsByUserId(String token, Long userId) {
        return webTestClient.get()
            .uri("/users/" + userId + "/friends")
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

    ResponseSpec joinMeeting(String token, Long meetingId) {
        return webTestClient.post()
            .uri("/meetings/" + meetingId + "/participants")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange();
    }

    ResponseSpec cancelMeeting(String token, Long meetingId) {
        return webTestClient.delete()
            .uri("/meetings/" + meetingId + "/participants")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange();
    }

    ResponseSpec addBookmark(String token, Long meetingId) {
        return webTestClient.post()
            .uri("/meetings/" + meetingId + "/bookmarks")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange();
    }

    ResponseSpec cancelBookmark(String token, Long meetingId) {
        return webTestClient.delete()
            .uri("/meetings/" + meetingId + "/bookmarks")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange();
    }

    ResponseSpec getCommonInterests(String token, List<Long> userIds) {
        var param = new StringBuilder();
        for (Long userId : userIds) {
            param.append(userId).append(",");
        }
        return webTestClient.get()
            .uri("/users/interests?user-id=" + param.substring(0, param.length() - 1))
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange();
    }

    ResponseSpec reissueJwt(String accessToken, String refreshToken) {
        return webTestClient.post()
            .uri("/auth/reissues")
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .header("Authorization-refresh", refreshToken)
            .exchange();
    }

    ResponseSpec withdrawUser(String accessToken, UserWithdrawRequest request) {
        return webTestClient
            .post()
            .uri("/users/withdraws")
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .bodyValue(request)
            .exchange();
    }

    ResponseSpec registerUserCard(String accessToken, UserRegisterRequest userRegisterRequest) {
        return webTestClient
            .post()
            .uri("/users")
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .bodyValue(userRegisterRequest)
            .exchange();
    }

    ResponseSpec logoutUser(String accessToken) {
        return webTestClient
            .post()
            .uri("/users/logouts")
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .exchange();
    }

    ResponseSpec getTeumteumAround(String accessToken, UserLocationRequest request) {
        return webTestClient
            .post()
            .uri("/teum-teum/arounds")
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .bodyValue(request)
            .exchange();
    }

    ResponseSpec deleteMeeting(String accessToken, Long meetingId) {
        return webTestClient
            .delete()
            .uri("/meetings/" + meetingId)
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .exchange();
    }

    ResponseSpec getUserReviews(String accessToken) {
        return webTestClient
            .get()
            .uri("/users/reviews")
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .exchange();
    }

    ResponseSpec registerUserReview(String accessToken, Long meetingId, ReviewRegisterRequest request) {
        String uri = "/users/reviews?meetingId=" + meetingId;
        return webTestClient
            .post()
            .uri(uri)
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .bodyValue(request)
            .exchange();
    }

    ResponseSpec getMeetingParticipants(String accessToken, Long meetingId) {
        return webTestClient
            .get()
            .uri("/meetings/" + meetingId + "/participants")
            .header(HttpHeaders.AUTHORIZATION, accessToken)
            .exchange();
    }
}
