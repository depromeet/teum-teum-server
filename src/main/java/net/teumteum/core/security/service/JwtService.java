package net.teumteum.core.security.service;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.auth.domain.response.TokenResponse;
import net.teumteum.core.property.JwtProperty;
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

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(this.jwtProperty.getAccess().getHeader()))
                .filter(StringUtils::hasText)
                .filter(accessToken -> accessToken.startsWith(jwtProperty.getBearer()))
                .map(accessToken -> accessToken.replace(jwtProperty.getBearer(), ""));
    }

    public String extractRefreshToken(HttpServletRequest request) {
        return request.getHeader(this.jwtProperty.getRefresh().getHeader());
    }

    public String createAccessToken(String payload) {
        return this.createToken(payload, this.jwtProperty.getAccess().getExpiration());
    }


    public String createRefreshToken() {
        return this.createToken(UUID.randomUUID().toString(), this.jwtProperty.getRefresh().getExpiration());

    }


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

    public TokenResponse createServiceToken(User users) {
        String accessToken = this.createAccessToken(String.valueOf(users.getId()));
        String refreshToken = this.createRefreshToken();

        this.redisService.setDataExpire(String.valueOf(users.getId()),
                refreshToken, this.jwtProperty.getRefresh().getExpiration());

        return new TokenResponse(this.jwtProperty.getBearer() + " " + accessToken, refreshToken, null);

    }

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
