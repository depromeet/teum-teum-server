package net.teumteum.integration;

import net.teumteum.core.security.UserAuthentication;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.security.auth.login.LoginContext;

@TestComponent
public class SecurityUtil {

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
