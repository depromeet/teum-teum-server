package net.teumteum.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.auth.domain.response.TokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping
@RequiredArgsConstructor
public class OAuthLoginController {

    private final net.teumteum.auth.service.OAuthService oAuthService;

    @GetMapping("/logins/callbacks/{provider}")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse oAuthLogin(
            @PathVariable String provider,
            @RequestParam String code) throws java.io.IOException {
        return oAuthService.oAuthLogin(provider, code);
    }
}
