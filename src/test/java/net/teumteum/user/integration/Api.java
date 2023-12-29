package net.teumteum.user.integration;

import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

@TestComponent
class Api {

    private final WebTestClient webTestClient;

    public Api(ApplicationContext applicationContext) {
        var controllers = applicationContext.getBeansWithAnnotation(Controller.class).values();
        webTestClient = WebTestClient.bindToController(controllers.toArray()).build();
    }

    ResponseSpec getUser(String token, Long userId) {
        return webTestClient.get()
            .uri("/users/" + userId)
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange();
    }

}