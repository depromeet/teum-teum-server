package net.teumteum.core.security;

import net.teumteum.Application;
import net.teumteum.core.security.service.SecurityService;
import net.teumteum.integration.Repository;
import net.teumteum.integration.SecurityContextSetting;
import net.teumteum.integration.TestLoginContext;
import net.teumteum.user.infra.GptTestServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {
    Application.class,
    Repository.class,
    GptTestServer.class,
    TestLoginContext.class,
    SecurityContextSetting.class})
@DisplayName("Security Service 테스트의")
public class SecurityServiceTest {

    @Autowired
    SecurityService securityService;

    @Autowired
    Repository repository;

    @Nested
    @DisplayName("Security Service 클래스는")
    class Security_service_test {

        @Test
        @DisplayName("UserAuthentication 객체를 SecurityContext 에 저장하고, userId를 반환한다.")
        void Set_user_authentication_and_return_user_id() {
            // given
            var existUser = repository.saveAndGetUser();
            SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(existUser));

            var savedAuthentication = SecurityContextHolder.getContext().getAuthentication();

            // when
            Long savedUserId = securityService.getCurrentUserId();

            // then
            Assertions.assertEquals(savedUserId, existUser.getId());
            Assertions.assertNotNull(savedAuthentication);
        }

        @Test
        @DisplayName("UserAuthentication 객체를 SecurityContext 에 저장하고, OAuthId를 반환한다.")
        void Set_user_authentication_return_oAuth_id() {
            // given
            var existUser = repository.saveAndGetUser();
            SecurityContextHolder.getContext().setAuthentication(new UserAuthentication(existUser));

            var savedAuthentication = SecurityContextHolder.getContext().getAuthentication();

            // when
            String savedOAuthId = securityService.getCurrentUserOAuthId();

            // then
            Assertions.assertEquals(savedOAuthId, existUser.getOauth().getOauthId());
            Assertions.assertNotNull(savedAuthentication);
        }
    }
}
