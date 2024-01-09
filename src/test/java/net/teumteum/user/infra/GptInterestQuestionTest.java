package net.teumteum.user.infra;

import java.util.List;
import net.teumteum.user.domain.InterestQuestion;
import net.teumteum.user.domain.UserFixture;
import net.teumteum.user.domain.response.InterestQuestionResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DisplayName("GptInterestQuestion 클래스의")
@ContextConfiguration(classes = {GptInterestQuestion.class, GptTestServer.class})
class GptInterestQuestionTest {

    @Autowired
    private GptTestServer gptTestServer;

    @Autowired
    private InterestQuestion interestQuestion;


    @Nested
    @DisplayName("getQuestion 메소드는")
    class GetQuestion_method {

        @Test
        @DisplayName("user 목록을 받아서, 관심 질문을 반환한다.")
        void Return_balance_game_when_receive_user_list() {
            // given
            var users = List.of(UserFixture.getDefaultUser(), UserFixture.getDefaultUser());
            var expected = new InterestQuestionResponse(
                "프로그래머",
                List.of("프론트엔드", "백엔드")
            );

            gptTestServer.enqueue(expected);
            gptTestServer.enqueue(expected);

            // when
            var result = interestQuestion.getQuestion(users);

            // then
            Assertions.assertThat(expected).isEqualTo(result);
        }

        @Test
        @DisplayName("Gpt 서버에서 관심목록 응답을 실패해도 5번까지 retry한다.")
        void Do_retry_when_gpt_server_cannot_receive_interests_lists() {
            // given
            var users = List.of(UserFixture.getDefaultUser(), UserFixture.getDefaultUser());
            var expected = new InterestQuestionResponse(
                "프로그래머",
                List.of("프론트엔드", "백엔드")
            );

            gptTestServer.enqueue400();
            gptTestServer.enqueue400();
            gptTestServer.enqueue400();
            gptTestServer.enqueue400();
            gptTestServer.enqueue(expected);

            // when
            var result = interestQuestion.getQuestion(users);

            // then
            Assertions.assertThat(expected).isEqualTo(result);
        }

        @Test
        @DisplayName("Gpt서버에서 관심목록 조회를 5번 초과로 실패하면 IllegalStateException 을 던진다.")
        void Throw_illegal_state_exception_exceed_5_time_to_get_common_interests() {
            // given
            var users = List.of(UserFixture.getDefaultUser(), UserFixture.getDefaultUser());
            gptTestServer.enqueue400();
            gptTestServer.enqueue400();
            gptTestServer.enqueue400();
            gptTestServer.enqueue400();
            gptTestServer.enqueue400();

            // when
            var result = Assertions.catchException(() -> interestQuestion.getQuestion(users));

            // then
            Assertions.assertThat(result.getClass()).isEqualTo(IllegalStateException.class);
        }
    }
}
