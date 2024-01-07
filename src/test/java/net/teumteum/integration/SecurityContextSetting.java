package net.teumteum.integration;

import net.teumteum.core.security.UserAuthentication;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserFixture;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@TestComponent
public class SecurityContextSetting {
    public void set() {
        User user = UserFixture.getIdUser();
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UserAuthentication(user));
        SecurityContextHolder.setContext(context);
    }
}
