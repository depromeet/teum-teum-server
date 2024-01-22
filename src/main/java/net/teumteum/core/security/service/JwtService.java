package net.teumteum.core.security.service;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.auth.domain.response.TokenResponse;
import net.teumteum.core.property.JwtProperty;
import net.teumteum.user.domain.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/* JWT 관련 모든 작업을 위한 Service */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService implements InitializingBean {

    private static final String TOKEN_SUBJECT = "ACCESSTOKEN";

    private final JwtProperty jwtProperty;
    private final RedisService redisService;
    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] secretKey = Decoders.BASE64.decode(jwtProperty.getSecret());
        key = Keys.hmacShaKeyFor(secretKey);
    }


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

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.get("id", Long.class);
        } catch (ExpiredJwtException exception) {
            return Long.valueOf(exception.getClaims().get("id").toString());
        }
    }


    public TokenResponse createServiceToken(User users) {
        String accessToken = createAccessToken(users.getId().toString());
        String refreshToken = createRefreshToken();

        this.redisService.setDataWithExpiration(String.valueOf(users.getId()), refreshToken,
            this.jwtProperty.getRefresh().getExpiration());

        return new TokenResponse(jwtProperty.getBearer() + " " + accessToken, refreshToken);
    }

    public String createAccessToken(String userId) {
        return this.createToken(userId, jwtProperty.getAccess().getExpiration());
    }

    public String createRefreshToken() {
        return this.createToken(UUID.randomUUID().toString(), jwtProperty.getRefresh().getExpiration());
    }

    private String createToken(String payLoad, Long tokenExpiration) {

        Date tokenExpiresIn = new Date(new Date().getTime() + tokenExpiration);
        return Jwts.builder()
            .setSubject(TOKEN_SUBJECT)
            .claim("id", payLoad)
            .signWith(key, HS512)
            .setExpiration(tokenExpiresIn)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            log.error("JWT 가 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 가 잘못되었습니다.");
        }
        return false;
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
