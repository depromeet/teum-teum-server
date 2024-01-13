package net.teumteum.core.security.service;

import lombok.RequiredArgsConstructor;
import net.teumteum.core.security.UserAuthentication;
import net.teumteum.user.domain.UserConnector;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserConnector userConnector;

    public static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private UserAuthentication getUserAuthentication() {
        return (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }


    public Long getCurrentUserId() {
        return getUserAuthentication() == null ? userConnector.findAllUser().get(0).getId()
            : getUserAuthentication().getId();
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
