package net.teumteum.auth.domain;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public abstract class OAuthUserInfo {

    protected Map<String, Object> attributes;

    public abstract String getOAuthId();

}
