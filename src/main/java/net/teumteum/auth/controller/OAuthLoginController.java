package net.teumteum.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.auth.domain.response.TokenResponse;
import net.teumteum.auth.service.OAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuthLoginController {

    private final OAuthService oAuthService;

    @GetMapping("/logins/callbacks/{provider}")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse oAuthLogin(@PathVariable String provider,
        @RequestParam String code,
        @RequestParam String state) {
        return oAuthService.oAuthLogin(provider, code, state);
    }

    @GetMapping("/favicon.ico")
    @ResponseStatus(HttpStatus.OK)
    public Void favicon() {
        return null;
    }
}
