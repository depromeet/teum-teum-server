package net.teumteum.core.security.service;

import net.teumteum.core.security.UserAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityService {
    private static UserAuthentication getUserAuthentication() {
        return (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    public Long getCurrentUserId() {
        UserAuthentication userAuthentication = getUserAuthentication();
        return userAuthentication == null ? 10000000000L : userAuthentication.getId();
    }


    public String getCurrentUserOAuthId() {
        UserAuthentication userAuthentication = getUserAuthentication();
        return userAuthentication.getOauthId();
    }

    public void setUserId(Long userId) {
        UserAuthentication userAuthentication = getUserAuthentication();
        userAuthentication.setUserId(userId);
    }
}
