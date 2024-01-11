package net.teumteum.auth.domain;

import java.util.Map;

public class KakaoOAuthUserInfo extends OAuthUserInfo {
    public KakaoOAuthUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getOAuthId() {
        return (String) attributes.get("id");
    }
}
