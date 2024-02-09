package net.teumteum.alert.domain;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@DisplayName("UserAlertRepository 클래스의")
class UserAlertRepositoryTest {

    @Autowired
    private UserAlertRepository alertRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    @DisplayName("findByUserIdWithLock 메소드는")
    class findByUserIdWithLock_method {

        @Test
        @DisplayName("userId로 userAlert를 조회한다.")
        void find_userAlert_by_userId() {
            // given
            var userId = 1L;
            var userAlert = new UserAlert(1L, userId, "token");

            alertRepository.saveAndFlush(userAlert);
            entityManager.clear();

            // when
            var result = alertRepository.findByUserIdWithLock(userId);

            // then
            Assertions.assertThat(result).isPresent();
        }
    }

}
