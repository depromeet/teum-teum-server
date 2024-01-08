package net.teumteum.integration;

import java.util.List;
import java.util.stream.LongStream;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.response.FriendsResponse;
import net.teumteum.user.domain.response.UserGetResponse;
import net.teumteum.user.domain.response.UsersGetByIdResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("유저 통합테스트의")
class UserIntegrationTest extends IntegrationTest {

    private static final String VALID_TOKEN = "VALID_TOKEN";
    private static final String INVALID_TOKEN = "IN_VALID_TOKEN";


    @Nested
    @DisplayName("유저 조회 API는")
    class Find_user_api {

        @Test
        @DisplayName("존재하는 유저의 id가 주어지면, 유저 정보를 응답한다.")
        void Return_user_info_if_exist_user_id_received() {
            // given
            var user = repository.saveAndGetUser();
            var expected = UserGetResponse.of(user);

            // when
            var result = api.getUser(VALID_TOKEN, user.getId());

            // then
            Assertions.assertThat(
                    result.expectStatus().isOk()
                        .expectBody(UserGetResponse.class)
                        .returnResult().getResponseBody())
                .usingRecursiveComparison()
                .isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 유저의 id가 주어지면, 400 Bad Request를 응답한다.")
        void Return_400_bad_request_if_not_exists_user_id_received() {
            // given
            var notExistUserId = 1L;

            // when
            var result = api.getUser(VALID_TOKEN, notExistUserId);

            // then
            result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
        }
    }

    @Nested
    @DisplayName("유저 id들로 유저들을 조회하는 API 는")
    class Find_users_by_user_ids_api {

        @Test
        @DisplayName("존재하는 유저의 id들로만 요청이 들어오면, 유저 정보를 응답한다.")
        void Return_user_info_if_exist_user_ids_received() {
            // given
            var user1 = repository.saveAndGetUser();
            var user2 = repository.saveAndGetUser();

            var expected = UsersGetByIdResponse.of(List.of(user1, user2));

            // when
            var result = api.getUsersById(VALID_TOKEN, user1.getId() + "," + user2.getId());

            // then
            Assertions.assertThat(result.expectStatus().isOk()
                .expectBody(UsersGetByIdResponse.class)
                .returnResult()
                .getResponseBody()
            ).usingRecursiveComparison().isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 유저의 id가 포함되어 있다면, 400 Bad Request 를 응답한다.")
        void Return_400_bad_request_if_not_exists_user_id_include() {
            // given
            var exist = repository.saveAndGetUser();
            var notExist = 999L;

            // when
            var result = api.getUsersById(VALID_TOKEN, exist.getId() + "," + notExist);

            // then
            result.expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("id가 비어있으면, 400 Bad Request 를 응답한다.")
        void Return_400_bad_request_if_empty_user_ids_input() {
            // when
            var result = api.getUsersById(VALID_TOKEN, "");

            // then
            result.expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("유저 수정 API는")
    class Update_user_api {

        @Test
        @DisplayName("수정할 회원의 정보가 주어지면, 회원 정보를 수정한다")
        void Update_user_info() {
            // given
            var existUser = repository.saveAndGetUser();
            List<User> allUser = repository.getAllUser();
            var updateUser = RequestFixture.userUpdateRequest(existUser);

            // when
            var result = api.updateUser(VALID_TOKEN, updateUser);

            // then
            result.expectStatus().isOk();
        }
    }

    @Nested
    @DisplayName("친구 맺기 API는")
    class Add_friends_api {

        @Test
        @DisplayName("존재하는 userId와 올바른 토큰이 주어지면, 친구를 맺고 200 OK를 반환한다.")
        void Return_200_ok_with_success_make_friends() {
            // given
            var me = repository.saveAndGetUser();
            var myToken = "JWT MY_TOKEN";
            var friend = repository.saveAndGetUser();

            // when
            var result = api.addFriends(myToken, friend.getId());

            // then
            result.expectStatus().isOk();
        }
    }

    @Nested
    @DisplayName("친구 조회 API는")
    class Find_friends_api {

        @Test
        @DisplayName("user의 id를 입력받으면, id에 해당하는 user의 친구 목록을 반환한다.")
        void Return_friends_when_received_user_id() {
            // given
            var me = repository.saveAndGetUser();
            var friend1 = repository.saveAndGetUser();
            var friend2 = repository.saveAndGetUser();

            loginContext.setUserId(me.getId());
            api.addFriends(VALID_TOKEN, friend1.getId());
            api.addFriends(VALID_TOKEN, friend2.getId());

            var expected = FriendsResponse.of(List.of(friend1, friend2));

            // when
            var result = api.getFriendsByUserId(VALID_TOKEN, me.getId());

            // then
            Assertions.assertThat(result.expectStatus().isOk()
                    .expectBody(FriendsResponse.class)
                    .returnResult()
                    .getResponseBody())
                .usingRecursiveComparison().isEqualTo(expected);
        }

        @Test
        @DisplayName("user의 id를 입력받았을때, 친구가 한명도 없다면, 빈 목록을 반환한다.")
        void Return_empty_friends_when_received_empty_friends_user_id() {
            // given
            var me = repository.saveAndGetUser();

            loginContext.setUserId(me.getId());

            var expected = FriendsResponse.of(List.of());

            // when
            var result = api.getFriendsByUserId(VALID_TOKEN, me.getId());

            // then
            Assertions.assertThat(result.expectStatus().isOk()
                    .expectBody(FriendsResponse.class)
                    .returnResult()
                    .getResponseBody())
                .usingRecursiveComparison().isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("공통 관심 질문 찾기 API는")
    class Find_common_interests_question_api {

        @Test
        @DisplayName("유저의 id들을 입력받고, 공통 관심사 질문을 반환한다.")
        void Return_common_interests_when_receive_user_ids() {
            // given
            var ids = LongStream.range(0, 4)
                .map(value -> repository.saveAndGetUser().getId())
                .boxed().toList();

            // when
            var result = api.getCommonInterests(VALID_TOKEN, ids);

            // then
            result.expectStatus().isOk();
        }
    }
}
