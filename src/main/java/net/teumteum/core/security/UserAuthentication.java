package net.teumteum.core.security;

import lombok.Getter;
import net.teumteum.user.domain.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Getter
public class UserAuthentication extends AbstractAuthenticationToken {

    private final String oauthId;
    private Long id;

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

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public void setUserId(Long userId) {
        id = userId;
    }
}
