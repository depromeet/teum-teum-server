package net.teumteum.core.security.service;

import net.teumteum.core.security.UserAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/* Security 관련 작업을 위한 서비스 */
@Component
public class SecurityService {
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /* 해당 요청에서 로그인한 회원 id 반환 */
    public Long getCurrentUserId() {
        UserAuthentication userAuthentication = getUserAuthentication();
        return userAuthentication.getId();
    }

    /* 해당 요청에서 로그인한 회원 OAuth id 반환 */
    public String getCurrentUserOAuthId() {
        UserAuthentication userAuthentication = getUserAuthentication();
        return userAuthentication.getOauthId();
    }

    public void setUserId(Long userId) {
        UserAuthentication userAuthentication = getUserAuthentication();
        userAuthentication.setUserId(userId);
    }

    private static UserAuthentication getUserAuthentication() {
        return (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
