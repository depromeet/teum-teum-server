package net.teumteum.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.auth.domain.response.TokenResponse;
import net.teumteum.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping
@RestController
@RequiredArgsConstructor
public class OAuthLoginController {

    private final AuthService authService;

    @PostMapping("/logins/callbacks/{provider}")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse oAuthLogin(
            @PathVariable String provider,
            @RequestParam String code
    ) {
        return authService.oAuthLogin(provider, code);
    }
}
