package net.teumteum.auth.domain;

import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class OAuthUserInfo {

    protected Map<String, Object> attributes;

    public abstract String getOAuthId();

}
