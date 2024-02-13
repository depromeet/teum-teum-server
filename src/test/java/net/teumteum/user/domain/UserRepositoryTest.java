package net.teumteum.user.domain;

import static net.teumteum.user.domain.Review.별로에요;
import static net.teumteum.user.domain.Review.좋아요;
import static net.teumteum.user.domain.Review.최고에요;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import net.teumteum.core.config.AppConfig;
import net.teumteum.user.domain.response.UserReviewResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(AppConfig.class)
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
            var existsUser = UserFixture.getNullIdUser();

            userRepository.saveAndFlush(existsUser);
            entityManager.clear();

            // when
            var result = userRepository.findById(existsUser.getId());

            // then
            Assertions.assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("value.createdAt", "value.updatedAt")
                .isEqualTo(Optional.ofNullable(existsUser));
        }
    }


    @Nested
    @DisplayName("countUserReviewsByUserId 메소드는")
    class CountUserReviewsByUserId_method {

        @Test
        @DisplayName("저장된 유저의 id 을 이용해서 유저의 리뷰 갯수를 조회하면, UserReviewResponse 을 반환한다.")
        void Count_user_reviews_by_user_id() {
            // given
            var id = 1L;
            var existUser = UserFixture.getUserWithId(id);

            userRepository.saveAndFlush(existUser);
            entityManager.clear();

            // when
            var result = userRepository.countUserReviewsByUser(existUser);

            // then
            Assertions.assertThat(result)
                .isNotEmpty()
                .hasSize(3)
                .extracting(UserReviewResponse::review,
                    UserReviewResponse::count)
                .contains(
                    Assertions.tuple(최고에요, 3L),
                    Assertions.tuple(별로에요, 1L),
                    Assertions.tuple(좋아요, 2L));

        }
    }
}
