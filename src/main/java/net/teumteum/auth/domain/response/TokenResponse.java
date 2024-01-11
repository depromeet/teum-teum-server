package net.teumteum.auth.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String oauthId;

    public TokenResponse(String accessToken, String refreshToken, String oauthId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.oauthId = oauthId;
    }

    public TokenResponse(String oauthId) {
        this.oauthId = oauthId;
    }
}
