package net.teumteum.core.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.auth.domain.response.TokenResponse;
import net.teumteum.core.property.JwtProperty;
import net.teumteum.user.domain.User;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/* JWT 관련 모든 작업을 위한 Service */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperty jwtProperty;
    private final RedisService redisService;

    public String extractAccessToken(HttpServletRequest request) {
        String accessToken = request.getHeader(jwtProperty.getAccess().getHeader());
        if (!ObjectUtils.isEmpty(accessToken)
            && accessToken.toLowerCase().startsWith(jwtProperty.getBearer().toLowerCase())) {
            return accessToken.substring(jwtProperty.getBearer().length()).trim();
        }
        return null;
    }

    public String extractRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader(jwtProperty.getRefresh().getHeader());
        if (!ObjectUtils.isEmpty(refreshToken)) {
            return refreshToken;
        }
        return null;
    }

    public String getUserIdFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(jwtProperty.getSecret())
                .parseClaimsJws(token).getBody().getSubject();
        } catch (Exception exception) {
            throw new JwtException("Access Token is not valid");
        }
    }

    public TokenResponse createServiceToken(User users) {
        String accessToken = createAccessToken(String.valueOf(users.getId()));
        String refreshToken = createRefreshToken();

        this.redisService.setDataExpire(String.valueOf(users.getId()), refreshToken,
            this.jwtProperty.getRefresh().getExpiration());

        return new TokenResponse(jwtProperty.getBearer() + " " + accessToken, refreshToken);

    }

    public String createAccessToken(String payload) {
        return this.createToken(payload, jwtProperty.getAccess().getExpiration());
    }

    public String createRefreshToken() {
        return this.createToken(UUID.randomUUID().toString(), jwtProperty.getRefresh().getExpiration());

    }

    private String createToken(String payload, Long tokenExpiration) {
        Claims claims = Jwts.claims().setSubject(payload);
        Date tokenExpiresIn = new Date(new Date().getTime() + tokenExpiration);

        return Jwts.builder().setClaims(claims).setIssuedAt(new Date()).setExpiration(tokenExpiresIn)
            .signWith(SignatureAlgorithm.HS512, jwtProperty.getSecret()).compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(jwtProperty.getSecret()).parseClaimsJws(token);
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

}
