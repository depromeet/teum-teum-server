package net.teumteum.core.property;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
@DisplayName("property 관련 테스트의 ")
@TestPropertySource(locations = {
        "classpath:application-auth.yml",
        "classpath:application-redis.yml"
})
@SpringBootTest
class PropertyTest {

    @Autowired
    private JwtProperty jwtProperty;

    @Autowired
    private RedisProperty redisProperty;

    @Nested
    @DisplayName("jwtProperty 주입 테스트가")
    class JwtPropertyTest {

        @Test
        @DisplayName("정상적으로 성공한다.")
        void test(){
            String bearer = jwtProperty.getBearer();
            System.out.println(bearer);
        }

    }

    @Nested
    @DisplayName("redisProperty 주입 테스트가")
    class RedisPropertyTest {
        @Test
        @DisplayName("정상적으로 성공한다.")
        void test(){
            String host = redisProperty.getHost();
            System.out.println(host);
        }
    }

}