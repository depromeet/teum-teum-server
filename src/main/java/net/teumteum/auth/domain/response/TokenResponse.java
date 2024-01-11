package net.teumteum.auth.domain.response;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
