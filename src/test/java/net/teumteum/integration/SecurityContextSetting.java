package net.teumteum.integration;

import static org.springframework.security.core.context.SecurityContextHolder.MODE_GLOBAL;
import static org.springframework.security.core.context.SecurityContextHolder.MODE_THREADLOCAL;

import net.teumteum.core.security.UserAuthentication;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserFixture;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@TestComponent
public class SecurityContextSetting {
    public void set(Long id) {
        SecurityContextHolder.setStrategyName(MODE_GLOBAL);
        User user = UserFixture.getUserWithId(id);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UserAuthentication(user));
        SecurityContextHolder.setContext(context);
    }

    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
        SecurityContextHolder.setStrategyName(MODE_THREADLOCAL);
    }
}
