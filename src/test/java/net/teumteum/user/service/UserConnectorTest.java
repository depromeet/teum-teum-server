package net.teumteum.user.service;

import java.util.Optional;
import net.teumteum.user.domain.UserConnector;
import net.teumteum.user.domain.UserFixture;
import net.teumteum.user.domain.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DisplayName("UserConnector 클래스의")
@ContextConfiguration(classes = UserConnectorImpl.class)
class UserConnectorTest {

    private static final Long EXIST_USER_ID = 1L;

    @Autowired
    private UserConnector userConnector;
    @MockBean
    private UserRepository userRepository;


    @BeforeEach
    void beforeEach() {
        Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(EXIST_USER_ID)).thenReturn(Optional.of(UserFixture.getDefaultUser()));
    }

    @Nested
    @DisplayName("findById 메소드는")
    class FindById_method {

        @Test
        @DisplayName("존재하는 user의 id가 들어오면, optional.user를 반환한다.")
        void Return_optional_user_if_exists_user_id() {
            // given
            var expect = Optional.of(UserFixture.getDefaultUser());

            // when
            var result = userConnector.findUserById(EXIST_USER_ID);

            // then
            Assertions.assertThat(result)
                .isPresent()
                .usingRecursiveComparison()
                .ignoringFields("value.oauth.oAuthAuthenticateInfo")
                .isEqualTo(expect);
        }
    }

}
