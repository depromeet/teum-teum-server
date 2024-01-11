package net.teumteum.auth.domain;

import java.util.Map;

public class NaverOAuthUserInfo extends OAuthUserInfo {
    public NaverOAuthUserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getOAuthId() {
        Map<String, Object> response = getResponse();
        if (response == null) {
            return null;
        }
        return (String) response.get("id");
    }

    private Map<String, Object> getResponse() {
        return (Map<String, Object>) attributes.get("response");
    }
}
