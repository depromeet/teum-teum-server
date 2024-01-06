package net.teumteum.integration;

import net.teumteum.core.context.LoginContext;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class TestLoginContext implements LoginContext {

    private Long userId;

    @Override
    public Long getUserId() {
        return this.userId;
    }

    @Override
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
