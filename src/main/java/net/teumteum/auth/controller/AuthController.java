package net.teumteum.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.teumteum.auth.domain.response.TokenResponse;
import net.teumteum.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissues")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse reissue(HttpServletRequest request) {
        return authService.reissue(request);
    }
}
