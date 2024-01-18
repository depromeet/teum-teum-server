package net.teumteum.integration;

import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserFixture;
import net.teumteum.user.domain.response.FriendsResponse;
import net.teumteum.user.domain.response.UserGetResponse;
import net.teumteum.user.domain.response.UserMeGetResponse;
import net.teumteum.user.domain.response.UserRegisterResponse;
import net.teumteum.user.domain.response.UsersGetByIdResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("유저 통합테스트의")
class UserIntegrationTest extends IntegrationTest {

    private static final String VALID_TOKEN = "VALID_TOKEN";
    private static final String INVALID_TOKEN = "IN_VALID_TOKEN";
    private static final Long DURATION = 3600000L;

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
    @DisplayName("내 정보 조회 API는")
    class Find_my_info_api {

        @Test
        @DisplayName("유효한 토큰이 주어지면, 내 정보를 응답한다.")
        void Return_my_info_if_valid_token_received() {
            // given
            var me = repository.saveAndGetUser();
            loginContext.setUserId(me.getId());

            var expected = UserMeGetResponse.of(me);

            // when
            var result = api.getMe(VALID_TOKEN);

            // then
            Assertions.assertThat(result.expectStatus().isOk()
                    .expectBody(UserMeGetResponse.class)
                    .returnResult()
                    .getResponseBody())
                .usingRecursiveComparison().isEqualTo(expected);
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
    @DisplayName("회원 탈퇴 API는")
    class Withdraw_user {

        @Test
        @DisplayName("현재 로그인한 회원을 탈퇴 처리한다.")
        void Withdraw_user_info_api() {
            // given
            var me = repository.saveAndGetUser();
            redisRepository.saveRedisDataWithExpiration(String.valueOf(me.getId()), VALID_TOKEN, DURATION);

            loginContext.setUserId(me.getId());

            // when & then

            assertThatCode(() -> api.withdrawUser(VALID_TOKEN))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("해당 회원이 존재하지 않으면, 500 에러를 반환한다.")
        void Return_500_error_if_user_not_exist() {
            // given
            repository.clearUserRepository();

            // when
            var result = api.withdrawUser(VALID_TOKEN);

            // then
            Assertions.assertThat(result.expectStatus().is5xxServerError()
                    .expectBody(ErrorResponse.class)
                    .returnResult()
                    .getResponseBody())
                .usingRecursiveComparison().isNull();
        }
    }

    @Nested
    @DisplayName("회원 카드 등록 API는")
    class Register_user_card_api {

        @Test
        @DisplayName("등록할 회원의 정보가 주어지면, 회원 정보를 저장한다.")
        void Register_user_info() {
            // given
            var UserRegister = RequestFixture.userRegisterRequest(UserFixture.getIdUser());
            // when
            var result = api.registerUserCard(VALID_TOKEN, UserRegister);

            // then
            Assertions.assertThat(result.expectStatus().isCreated()
                    .expectBody(UserRegisterResponse.class)
                    .returnResult()
                    .getResponseBody())
                .usingRecursiveComparison().isNotNull();
        }

        @Test
        @DisplayName("이미 존재하는 회원인 경우, 400 Bad Request 을 반환한다 ")
        void Return_400_badRequest_register_user_card() {
            // given
            var existUser = repository.saveAndGetUser();

            var userRegister = RequestFixture.userRegisterRequestWithFail(existUser);
            // when
            var result = api.registerUserCard(VALID_TOKEN, userRegister);

            // then
            var responseBody = result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult().getResponseBody();

            Assertions.assertThat(responseBody)
                .isNotNull();
        }

        @Test
        @DisplayName("요청 값의 유효성 검사가 실패하면, 400 에러를 반환한다.")
        void Return_400_badRequest_if_not_meet_request_condition() {
            // given
            var existUser = repository.saveAndGetUser();

            var userRegister = RequestFixture.userRegisterRequestWithNoValid(existUser);
            // when
            var result = api.registerUserCard(VALID_TOKEN, userRegister);

            // then
            ErrorResponse responseBody = result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult().getResponseBody();

            Assertions.assertThat(responseBody).isNull();
        }
    }

    @Nested
    @DisplayName("회원 로그아웃 API 는")
    class Logout_user_api {

        @Test
        @DisplayName("현재 로그인된 유저를 로그아웃 시킨다.")
        void Logout_user() {
            // given
            var existUser = repository.saveAndGetUser();
            redisRepository.saveRedisDataWithExpiration(String.valueOf(existUser.getId()), VALID_TOKEN, DURATION);

            // when & then
            assertThatCode(() -> api.logoutUser(VALID_TOKEN))
                .doesNotThrowAnyException();
        }
    }
}
