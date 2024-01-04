package net.teumteum.core.security;

import lombok.Getter;
import net.teumteum.user.domain.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * - Security Context Holder 에 주입되는 Authentication 을 구현한 AbstractAuthenticationToken
 * - 인증 후 UserAuthentication 을 SecurityContext 에 저장 시, Security Context Holder 로 어디서든 접근 가능 !!
 **/

@Getter
public class UserAuthentication extends AbstractAuthenticationToken {

    private Long id;
    private final String oauthId;

    public UserAuthentication(User user) {
        super(authorities(user));
        this.id = user.getId();
        this.oauthId = user.getOauth().getOauthId();
    }
    private static List<GrantedAuthority> authorities(User User) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(User.getRoleType().name()));
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return id;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    public void setUserId(Long userId) {
        id = userId;
    }
}
