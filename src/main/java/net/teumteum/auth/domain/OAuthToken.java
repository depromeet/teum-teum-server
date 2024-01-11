package net.teumteum.auth.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthToken(
        @JsonProperty("token_type")
        String tokenType,
        @JsonProperty("access_token")
        String accessToken,
        String scope,

        @JsonProperty("expires_in")
        Integer expiresIn
) {
    public String getTokenType() {
        return this.tokenType;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getScope() {
        return this.scope;
    }

    public Integer getExpiresIn() {
        return this.expiresIn;
    }
}
