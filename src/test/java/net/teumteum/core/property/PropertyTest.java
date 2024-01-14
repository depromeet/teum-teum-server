package net.teumteum.core.property;

import net.teumteum.Application;
import net.teumteum.integration.SecurityContextSetting;
import net.teumteum.integration.TestLoginContext;
import net.teumteum.user.infra.GptTestServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;


@SpringBootTest
@ContextConfiguration(classes = {
    Application.class,
    GptTestServer.class,
    TestLoginContext.class,
    SecurityContextSetting.class})
@DisplayName("Property 설정 클래스의")
class PropertyTest {

    @Autowired
    RedisProperty redisProperty;

    @Autowired
    JwtProperty jwtProperty;


    @Nested
    @DisplayName("RedisProperty 클래스는")
    class Read_redis_value_from_application_yml {

        @Test
        @DisplayName("RedisProperty 클래스가 application.yml 에서 설정 값을 정상적으로 읽어온다.")
        void Make_redis_property_from_application_yml() {
            // given
            String expectedHost = "localhost";
            int expectedPort = 6378;

            // when & then
            Assertions.assertEquals(expectedHost, redisProperty.getHost());
            Assertions.assertEquals(expectedPort, redisProperty.getPort());
        }
    }

    @Nested
    @DisplayName("JwtProperty 클래스는")
    class Read_jwt_value_from_application_yml {

        @Test
        @DisplayName("JwtProperty 클래스가 application.yml 에서 설정 값을 정상적으로 읽어온다.")
        void Make_jwt_property_from_application_yml() {
            // given
            String expectedBearer = "Bearer";
            String expectedSecret = "secret";

            // when & then
            Assertions.assertEquals(expectedBearer, jwtProperty.getBearer());
            Assertions.assertEquals(expectedSecret, jwtProperty.getSecret());
        }
    }
}
