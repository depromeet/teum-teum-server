package net.teumteum.user.domain;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayName("UserRepository 클래스의")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    @DisplayName("save 메소드는")
    class Save_method {

        @Test
        @DisplayName("올바른 UserEntity가 들어오면, 유저 저장에 성공한다.")
        void Save_success_if_correct_user_entered() {
            // given
            var newUser = UserFixture.getDefaultUser();

            // when
            var result = Assertions.catchException(() -> userRepository.saveAndFlush(newUser));

            // then
            Assertions.assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("findById 메소드는")
    class FindById_method {

        @Test
        @DisplayName("저장된 유저의 id로 조회하면, 유저를 반환한다.")
        void Find_success_if_exists_user_id_input() {
            // given
            var id = 1L;
            var existsUser = UserFixture.getUserWithId(id);

            userRepository.saveAndFlush(existsUser);
            entityManager.clear();

            // when
            var result = userRepository.findById(id);

            // then
            Assertions.assertThat(result)
                .isPresent()
                .usingRecursiveComparison()
                .ignoringFields("value.createdAt", "value.updatedAt")
                .isEqualTo(Optional.of(existsUser));
        }
    }

}
