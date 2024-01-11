package net.teumteum.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.auth.domain.CustomOAuthUser;
import net.teumteum.auth.domain.OAuthUserAttributes;
import net.teumteum.core.security.Authenticated;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserConnector;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import static net.teumteum.core.security.Authenticated.네이버;
import static net.teumteum.core.security.Authenticated.카카오;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private static final String NAVER = "naver";
    private static final String KAKAO = "kakao";

    private final UserConnector userConnector;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String providerId = userRequest.getClientRegistration().getRegistrationId();
        Authenticated authenticated = getAuthenticated(providerId);
        String userNameAttributeName
                = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthUserAttributes oAuthUserAttributes
                = OAuthUserAttributes.of(authenticated, userNameAttributeName, oAuth2User.getAttributes());

        User user = getUser(oAuthUserAttributes, authenticated);

        return new CustomOAuthUser(user, oAuth2User);
    }

    private Authenticated getAuthenticated(String providerId) {
        if (NAVER.equals(providerId)) {
            return 네이버;
        }
        return 카카오;
    }

    private User getUser(OAuthUserAttributes oAuthUserAttributes, Authenticated authenticated) {
        return userConnector.findByAuthenticatedAndOAuthId(authenticated,
                oAuthUserAttributes.getOAuthUserInfo().getOAuthId()).orElse(null);
    }
}
