package net.teumteum.auth.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.teumteum.core.security.Authenticated.네이버;
import static net.teumteum.core.security.Authenticated.카카오;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.auth.domain.KakaoOAuthUserInfo;
import net.teumteum.auth.domain.NaverOAuthUserInfo;
import net.teumteum.auth.domain.OAuthToken;
import net.teumteum.auth.domain.OAuthUserInfo;
import net.teumteum.auth.domain.response.TokenResponse;
import net.teumteum.core.security.Authenticated;
import net.teumteum.core.security.service.JwtService;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserConnector;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {

    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";

    private final InMemoryClientRegistrationRepository inMemoryClientRegistrationRepository;
    private final JwtService jwtService;

    private final UserConnector userConnector;


    public TokenResponse oAuthLogin(String registrationId, String code, String state) {
        ClientRegistration clientRegistration = inMemoryClientRegistrationRepository.findByRegistrationId(
            registrationId);
        Authenticated authenticated = getAuthenticated(clientRegistration.getRegistrationId());
        OAuthUserInfo oAuthUserInfo = getOAuthUserInfo(clientRegistration, authenticated, code, state);
        return makeResponse(oAuthUserInfo, authenticated);
    }

    private Authenticated getAuthenticated(String registrationId) {
        if (registrationId.equals(NAVER)) {
            return 네이버;
        }
        return 카카오;
    }

    private OAuthUserInfo getOAuthUserInfo(ClientRegistration clientRegistration, Authenticated authenticated,
        String code, String state) {
        Map<String, Object> oAuthAttribute = getOAuthAttribute(clientRegistration,
            getToken(clientRegistration, code, state));
        if (authenticated == 네이버) {
            return new NaverOAuthUserInfo(oAuthAttribute);
        }
        return new KakaoOAuthUserInfo(oAuthAttribute);
    }

    private TokenResponse makeResponse(OAuthUserInfo oAuthUserInfo, Authenticated authenticated) {
        String oauthId = oAuthUserInfo.getOAuthId();

        return getUser(oauthId, authenticated)
            .map(jwtService::createServiceToken)
            .orElseGet(() -> new TokenResponse(oauthId));
    }

    private OAuthToken getToken(ClientRegistration clientRegistration, String code, String state) {
        return WebClient.create().post()
            .uri(clientRegistration.getProviderDetails().getTokenUri())
            .headers(header -> {
                header.setContentType(APPLICATION_FORM_URLENCODED);
                header.setAcceptCharset(Collections.singletonList(UTF_8));
            }).bodyValue(tokenRequest(clientRegistration, code, state))
            .retrieve()
            .bodyToMono(OAuthToken.class).block();
    }

    private Map<String, Object> getOAuthAttribute(ClientRegistration clientRegistration, OAuthToken oAuthToken) {
        return WebClient.create().get()
            .uri(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri())
            .headers(header -> header.setBearerAuth(oAuthToken.accessToken())).retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
            }).block();
    }


    private Optional<User> getUser(String oauthId, Authenticated authenticated) {
        return this.userConnector.findByAuthenticatedAndOAuthId(authenticated, oauthId);
    }

    private MultiValueMap<String, String> tokenRequest(ClientRegistration clientRegistration, String code,
        String state) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", clientRegistration.getAuthorizationGrantType().getValue());
        formData.add("redirect_uri", clientRegistration.getRedirectUri());
        formData.add("client_secret", clientRegistration.getClientSecret());
        formData.add("client_id", clientRegistration.getClientId());
        formData.add("state", URLEncoder.encode(state, UTF_8));
        return formData;
    }
}
