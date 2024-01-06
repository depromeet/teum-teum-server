package net.teumteum.core.security.service;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.core.property.JwtProperty;
import net.teumteum.core.security.dto.TokenResponse;
import net.teumteum.user.domain.User;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/* JWT 관련 모든 작업을 위한 Service */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final JwtProperty jwtProperty;
    private final RedisService redisService;

    // HttpServletRequest 부터 Access Token 추출
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(this.jwtProperty.getAccess().getHeader()))
                .filter(StringUtils::hasText)
                .filter(accessToken -> accessToken.startsWith(jwtProperty.getBearer()))
                .map(accessToken -> accessToken.replace(jwtProperty.getBearer(), ""));
    }

    // HttpServletRequest 부터 Refresh Token 추출
    public String extractRefreshToken(HttpServletRequest request) {
        return request.getHeader(this.jwtProperty.getRefresh().getHeader());
    }

    // access token 생성
    public String createAccessToken(String payload) {
        return this.createToken(payload, this.jwtProperty.getAccess().getExpiration());
    }


    // refresh token 생성
    public String createRefreshToken() {
        return this.createToken(UUID.randomUUID().toString(), this.jwtProperty.getRefresh().getExpiration());

    }


    // access token 으로부터 회원 아이디 추출
    public String getUserIdFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(this.jwtProperty.getSecret())
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception exception) {
            throw new JwtException("Access Token is not valid");
        }
    }

    // kakao oauth 로그인 & 일반 로그인 시 jwt 응답 생성 + redis refresh 저장
    public TokenResponse createServiceToken(User users) {
        String accessToken = this.createAccessToken(String.valueOf(users.getId()));
        String refreshToken = this.createRefreshToken();

        /* 서비스 토큰 생성 */
        TokenResponse userServiceTokenResponseDto = TokenResponse.builder()
                .accessToken(this.jwtProperty.getBearer() + " " + accessToken)
                .refreshToken(refreshToken)
                .build();

        /* redis refresh token 저장 */
        this.redisService.setDataExpire(String.valueOf(users.getId()),
                userServiceTokenResponseDto.getRefreshToken(), this.jwtProperty.getRefresh().getExpiration());

        return userServiceTokenResponseDto;
    }

    // token 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(this.jwtProperty.getSecret()).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException exception) {
            log.warn("만료된 jwt 입니다.");
        } catch (UnsupportedJwtException exception) {
            log.warn("지원되지 않는 jwt 입니다.");
        } catch (IllegalArgumentException exception) {
            log.warn("jwt 에 오류가 존재합니다.");
        }
        return false;
    }

    // 실제 token 생성 로직
    private String createToken(String payload, Long tokenExpiration) {
        Claims claims = Jwts.claims().setSubject(payload);
        Date tokenExpiresIn = new Date(new Date().getTime() + tokenExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(tokenExpiresIn)
                .signWith(SignatureAlgorithm.HS512, this.jwtProperty.getSecret())
                .compact();
    }
}
