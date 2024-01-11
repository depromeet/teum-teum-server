package net.teumteum.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.auth.domain.CustomOAuthUser;
import net.teumteum.auth.domain.OAuthToken;
import net.teumteum.auth.domain.response.TokenResponse;
import net.teumteum.core.security.service.JwtService;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserConnector;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final OAuthService oAuthService;
    private final UserConnector userConnector;

    public Optional<User> findUserByToken(String accessToken) {
        Long id = Long.parseLong(jwtService.getUserIdFromToken(accessToken));
        return userConnector.findUserById(id);
    }

    public TokenResponse oAuthLogin(String provider, String code) {
        log.info("provider is {}", provider);
        OAuthToken oAuthToken = getOAuthToken(null, code);
        CustomOAuthUser oAuth2User = getCustomOAuthUser(oAuthToken.getAccessToken(), oAuthToken, null);
        return jwtService.createServiceToken(oAuth2User.getUser());
    }

    private CustomOAuthUser getCustomOAuthUser(String accessToken, OAuthToken oAuthToken, ClientRegistration clientRegistration) {
        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken, Instant.now(),
                Instant.ofEpochMilli(oAuthToken.getExpiresIn()));

        OAuth2UserRequest oAuth2UserRequest = new OAuth2UserRequest(clientRegistration, oAuth2AccessToken);
        return (CustomOAuthUser) oAuthService.loadUser(oAuth2UserRequest);
    }

    private OAuthToken getOAuthToken(ClientRegistration clientRegistration, String code) {
        return WebClient.create()
                .post()
                .uri(clientRegistration.getProviderDetails().getTokenUri())
                .headers(header -> {
                    header.setContentType(APPLICATION_FORM_URLENCODED);
                    header.setAcceptCharset(Collections.singletonList(UTF_8));
                })
                .bodyValue(Objects.requireNonNull(getOAuthTokenRequest(clientRegistration, code)))
                .retrieve()
                .bodyToMono(OAuthToken.class)
                .block();
    }

    private MultiValueMap<String, String> getOAuthTokenRequest(ClientRegistration clientRegistration, String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", clientRegistration.getRedirectUri());
        formData.add("client_secret", clientRegistration.getClientSecret());
        formData.add("client_id", clientRegistration.getClientId());
        return formData;
    }
}
