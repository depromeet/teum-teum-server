package net.teumteum.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.auth.domain.response.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@RequestMapping
@RequiredArgsConstructor
public class OAuthLoginController {

    private final net.teumteum.auth.service.OAuthService oAuthService;

    @GetMapping("/logins/callbacks/{provider}")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse oAuthLogin(
        @PathVariable String provider,
        @RequestParam String code) {
        return oAuthService.oAuthLogin(provider, code);
    }
}
