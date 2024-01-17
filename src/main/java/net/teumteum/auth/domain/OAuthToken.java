package net.teumteum.auth.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthToken(
    @JsonProperty("access_token")
    String accessToken,
    String scope,
    @JsonProperty("token_type")
    String tokenType
) {

}
