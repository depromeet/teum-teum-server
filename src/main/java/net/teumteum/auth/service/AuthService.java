package net.teumteum.auth.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.auth.domain.response.TokenResponse;
import net.teumteum.core.security.service.JwtService;
import net.teumteum.core.security.service.RedisService;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserConnector;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final RedisService redisService;
    private final UserConnector userConnector;

    public Optional<User> findUserByToken(String accessToken) {
        Long id = Long.parseLong(jwtService.getUserIdFromToken(accessToken));
        return userConnector.findUserById(id);
    }

    public TokenResponse reissue(HttpServletRequest request) {
        String refreshToken = jwtService.extractRefreshToken(request);
        String accessToken = jwtService.extractAccessToken(request);

        checkRefreshTokenValidation(refreshToken);

        User user = findUserByToken(accessToken)
            .orElseThrow(() -> new IllegalArgumentException("access token 에 해당하는 user를 찾을 수 없습니다."));

        checkRefreshTokenMatch(user, refreshToken);
        return issueNewToken(user);
    }

    private void checkRefreshTokenValidation(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new IllegalArgumentException("refresh token 이 유효하지 않습니다.");
        }
    }

    private void checkRefreshTokenMatch(User user, String refreshToken) {
        String savedRefreshToken = redisService.getData(String.valueOf(user.getId()));
        if (!savedRefreshToken.equals(refreshToken)) {
            throw new IllegalArgumentException("refresh token 이 일치하지 않습니다.");
        }
    }


    private TokenResponse issueNewToken(User user) {
        return new TokenResponse(jwtService.createAccessToken(user.getOauth().getOauthId()),
            jwtService.createRefreshToken());
    }
}
