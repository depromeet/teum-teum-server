package net.teumteum.user.domain.response;

import net.teumteum.auth.domain.response.TokenResponse;

public record UserRegisterResponse(
    Long id,
    String accessToken,
    String refreshToken
) {

    public static UserRegisterResponse of(Long id, TokenResponse tokenResponse) {
        return new UserRegisterResponse(id, tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());
    }
}
