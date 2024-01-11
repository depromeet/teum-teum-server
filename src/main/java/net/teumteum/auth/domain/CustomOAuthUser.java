package net.teumteum.auth.domain;

import lombok.Getter;
import net.teumteum.user.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuthUser implements OAuth2User {

    private final User user;
    private final Map<String, Object> attributes;

    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuthUser(User user, OAuth2User oAuth2User) {
        this.user = user;
        this.attributes = oAuth2User.getAttributes();
        this.authorities = oAuth2User.getAuthorities();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    public Long getUserId() {
        return user.getId();
    }
}
