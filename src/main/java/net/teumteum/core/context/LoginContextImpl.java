package net.teumteum.core.context;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@Profile("prod")
@RequestScope(proxyMode = ScopedProxyMode.INTERFACES)
public class LoginContextImpl implements LoginContext {

    private Long userId;

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public Long getUserId() {
        return userId;
    }

}
