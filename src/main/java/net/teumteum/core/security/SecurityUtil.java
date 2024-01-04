package net.teumteum.core.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    /* 해당 요청에서 로그인한 회원 id 반환 */
    public Long getCurrentUserId() {
        UserAuthentication userAuthentication
                = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return userAuthentication.getId();
    }

    /* 해당 요청에서 로그인한 회원 OAuth id 반환 */
    public String getCurrentUserOAuthId() {
        UserAuthentication userAuthentication
                = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();
        return userAuthentication.getOauthId();
    }
}
