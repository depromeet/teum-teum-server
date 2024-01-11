package net.teumteum.auth.domain;

import lombok.Builder;
import lombok.Getter;
import net.teumteum.core.security.Authenticated;

import java.util.Map;

import static net.teumteum.core.security.Authenticated.네이버;

@Getter
public class OAuthUserAttributes {

    private final String nameAttributeKey;
    private final OAuthUserInfo oAuthUserInfo;

    @Builder
    private OAuthUserAttributes(String nameAttributeKey, OAuthUserInfo oAuthUserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuthUserInfo = oAuthUserInfo;
    }

    public static OAuthUserAttributes of(Authenticated authenticated,
                                         String userNameAttributeName, Map<String, Object> attributes) {
        if (authenticated == 네이버) {
            return ofNaver(userNameAttributeName, attributes);
        }
        return ofKakao(userNameAttributeName, attributes);
    }


    private static OAuthUserAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthUserAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuthUserInfo(new NaverOAuthUserInfo(attributes))
                .build();
    }

    private static OAuthUserAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthUserAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oAuthUserInfo(new KakaoOAuthUserInfo(attributes))
                .build();
    }
}
