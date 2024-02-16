package net.teumteum.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.meeting.domain.Meeting;
import net.teumteum.meeting.domain.Topic;
import net.teumteum.meeting.domain.response.MeetingResponse;
import net.teumteum.meeting.domain.response.MeetingsResponse;
import net.teumteum.meeting.model.PageDto;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DisplayName("미팅 통합테스트의")
class MeetingIntegrationTest extends IntegrationTest {

    public static final int DEFAULT_QUERY_SIZE = 5;
    public static final Pageable FIRST_PAGE_NATION = PageRequest.of(0, DEFAULT_QUERY_SIZE, Sort.Direction.DESC, "id");
    private static final String VALID_TOKEN = "VALID_TOKEN";
    private static final String INVALID_TOKEN = "IN_VALID_TOKEN";

    @Nested
    @DisplayName("단일 미팅 조회 API는")
    class Find_meeting_api {

        @Test
        @DisplayName("존재하는 모임의 id가 주어지면, 모임 정보를 응답한다.")
        void Return_meeting_info_if_exist_meeting_id_received() {
            // given
            var user = repository.saveAndGetUser();
            securityContextSetting.set(user.getId());
            var meeting = repository.saveAndGetOpenMeeting();
            var expected = MeetingResponse.of(meeting, false);
            // when
            var result = api.getMeetingById(VALID_TOKEN, meeting.getId());
            // then
            assertThat(
                result.expectStatus().isOk()
                    .expectBody(MeetingResponse.class)
                    .returnResult().getResponseBody())
                .usingRecursiveComparison()
                .isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 모임의 id가 주어지면, 400 Bad Request를 응답한다.")
        void Return_400_bad_request_if_not_exists_meeting_id_received() {
            // given
            var user = repository.saveAndGetUser();
            securityContextSetting.set(user.getId());
            var notExistMeetingId = 1L;
            // when
            var result = api.getMeetingById(VALID_TOKEN, notExistMeetingId);
            // then
            result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
        }

        @Test
        @DisplayName("유저가 북마크한 모임이라면, isBookmarked를 true로 응답한다.")
        void Return_is_bookmarked_true_if_user_bookmarked_meeting() {
            // given
            var user = repository.saveAndGetUser();
            securityContextSetting.set(user.getId());
            var meeting = repository.saveAndGetOpenMeeting();
            api.addBookmark(VALID_TOKEN, meeting.getId());
            // when
            var result = api.getMeetingById(VALID_TOKEN, meeting.getId());
            // then
            assertThat(
                result.expectStatus().isOk()
                    .expectBody(MeetingResponse.class)
                    .returnResult().getResponseBody())
                .extracting(MeetingResponse::isBookmarked)
                .isEqualTo(true);
        }
    }

    @Nested
    @DisplayName("미팅 삭제 API는")
    class Delete_meeting_api {

        @Test
        @DisplayName("존재하는 모임의 id가 주어지면, 모임을 삭제한다.")
        void Delete_meeting_if_exist_meeting_id_received() {
            // given
            var host = repository.saveAndGetUser();
            securityContextSetting.set(host.getId());

            var meeting = repository.saveAndGetOpenMeetingWithHostId(host.getId());
            // when
            var result = api.deleteMeeting(VALID_TOKEN, meeting.getId());
            // then
            result.expectStatus().isOk();
        }

        @Test
        @DisplayName("종료된 모임의 id가 주어지면, 400 Bad Request를 응답한다.")
        void Return_400_bad_request_if_closed_meeting_id_received() {
            // given
            var host = repository.saveAndGetUser();
            securityContextSetting.set(host.getId());
            var meeting = repository.saveAndGetCloseMeetingWithHostId(host.getId());
            // when
            var result = api.deleteMeeting(VALID_TOKEN, meeting.getId());
            // then
            result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
        }

        @Test
        @DisplayName("hostId와 userId가 다르면, 400 Bad Request를 응답한다.")
        void Return_400_bad_request_if_hostId_and_userId_are_different() {
            // given
            var user = repository.saveAndGetUser();
            securityContextSetting.set(user.getId());

            var host = repository.saveAndGetUser();
            var meeting = repository.saveAndGetOpenMeetingWithHostId(host.getId());
            // when
            var result = api.deleteMeeting(VALID_TOKEN, meeting.getId());
            // then
            result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
        }
    }

    @Nested
    @DisplayName("미팅 목록 조회 API는")
    class Find_meeting_list_api {

        @Test
        @DisplayName("존재하는 topic이 주어지면 페이지 네이션을 적용해 미팅 목록을 최신순으로 응답한다.")
        void Return_meeting_list_if_topic_and_page_nation_received() {
            // given
            var user = repository.saveAndGetUser();
            securityContextSetting.set(user.getId());
            var size = 2;
            var openMeetingsByTopic = repository.saveAndGetOpenMeetingsByTopic(size, Topic.스터디);
            var closeTopicMeetingsByTopic = repository.saveAndGetOpenMeetingsByTopic(size, Topic.고민_나누기);

            var expectedData = MeetingsResponse.of(
                openMeetingsByTopic.stream()
                    .sorted(Comparator.comparing(Meeting::getId).reversed())
                    .toList()
            );

            var expected = PageDto.of(expectedData, false);

            // when
            var result = api.getMeetingsByTopic(VALID_TOKEN, FIRST_PAGE_NATION, true, Topic.스터디);
            // then
            assertThat(
                result.expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<PageDto<MeetingsResponse>>() {
                    })
                    .returnResult().getResponseBody())
                .usingRecursiveComparison()
                .isEqualTo(expected);
        }

        @Test
        @DisplayName("제목이나 설명에 존재하는 검색어가 주어지면 페이지 네이션을 적용해 미팅 목록을 최신순으로 응답한다.")
        void Return_meeting_list_if_search_word_and_page_nation_received() {
            // given
            var user = repository.saveAndGetUser();
            securityContextSetting.set(user.getId());
            var size = 2;
            var openMeetingsByTitle = repository.saveAndGetOpenMeetingsByTitle(size, "개발자 스터디");
            var closeMeetingsByTitle = repository.saveAndGetCloseMeetingsByTitle(size, "개발자 스터디");
            var openMeetingsByIntroduction = repository.saveAndGetOpenMeetingsByIntroduction(size,
                "개발자 스터디에 대한 설명입니다.");
            var closeMeetingsByIntroduction = repository.saveAndGetCloseMeetingsByIntroduction(size,
                "개발자 스터디에 대한 설명입니다.");

            var expectedData = MeetingsResponse.of(Stream.of(openMeetingsByIntroduction, openMeetingsByTitle)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparing(Meeting::getId).reversed())
                .toList()
            );

            var expected = PageDto.of(expectedData, false);

            // when
            var result = api.getMeetingsByTopic(VALID_TOKEN, FIRST_PAGE_NATION, true, Topic.스터디);
            // then
            assertThat(
                result.expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<PageDto<MeetingsResponse>>() {
                    })
                    .returnResult().getResponseBody())
                .usingRecursiveComparison()
                .isEqualTo(expected);
        }

        @Test
        @DisplayName("참여자 id가 주어지면 페이지 네이션을 적용해 미팅 목록을 최신순으로 응답한다.")
        void Return_meeting_list_if_participant_user_id_and_page_nation_received() {
            // given
            var user = repository.saveAndGetUser();
            securityContextSetting.set(user.getId());
            var size = 2;
            var openMeetingsByParticipantUserId = repository.saveAndGetOpenMeetingsByParticipantUserId(size, 2L);
            var closeMeetingsByParticipantUserId = repository.saveAndGetCloseMeetingsByParticipantUserId(size, 2L);

            var expectedData = MeetingsResponse.of(
                openMeetingsByParticipantUserId.stream()
                    .sorted(Comparator.comparing(Meeting::getId).reversed())
                    .toList()
            );

            var expected = PageDto.of(expectedData, false);

            // when
            var result = api.getMeetingsByTopic(VALID_TOKEN, FIRST_PAGE_NATION, true, Topic.스터디);
            // then
            assertThat(
                result.expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<PageDto<MeetingsResponse>>() {
                    })
                    .returnResult().getResponseBody())
                .usingRecursiveComparison()
                .isEqualTo(expected);
        }

        @Test
        @DisplayName("요청한 size와 page보다 더 많은 데이터가 존재하면, hasNext를 true로 응답한다.")
        void Return_has_next_true_if_more_data_exists_than_requested_size_and_page() {
            // given
            var user = repository.saveAndGetUser();
            securityContextSetting.set(user.getId());
            var size = 10;
            var openMeetingsByTopic = repository.saveAndGetOpenMeetingsByTopic(size, Topic.스터디);

            var expectedData = MeetingsResponse.of(
                openMeetingsByTopic.stream()
                    .sorted(Comparator.comparing(Meeting::getId).reversed())
                    .toList()
                    .subList(0, DEFAULT_QUERY_SIZE)
            );

            var expected = PageDto.of(expectedData, true);

            // when
            var result = api.getMeetingsByTopic(VALID_TOKEN, FIRST_PAGE_NATION, true, Topic.스터디);
            // then
            assertThat(
                result.expectStatus().isOk()
                    .expectBody(new ParameterizedTypeReference<PageDto<MeetingsResponse>>() {
                    })
                    .returnResult().getResponseBody())
                .usingRecursiveComparison()
                .isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("미팅 참여 API는")
    class Join_meeting_api {

        @Test
        @DisplayName("존재하는 모임의 id가 주어지면, 모임에 참여한다.")
        void Join_meeting_if_exist_meeting_id_received() {
            // given
            var me = repository.saveAndGetUser();
            var existMeeting = repository.saveAndGetOpenMeeting();

            securityContextSetting.set(me.getId());
            // when
            var result = api.joinMeeting(VALID_TOKEN, existMeeting.getId());
            // then
            assertThat(
                result.expectStatus().isCreated()
                    .expectBody(MeetingResponse.class)
                    .returnResult()
                    .getResponseBody())
                .extracting(MeetingResponse::participantIds)
                .has(new Condition<>(ids -> ids.contains(me.getId()), "참여자 목록에 나를 포함한다.")
                );
        }

        @Test
        @DisplayName("이미 참여한 모임의 id가 주어지면, 400 Bad Request를 응답한다.")
        void Return_400_bad_request_if_already_joined_meeting_id_received() {
            // given
            var me = repository.saveAndGetUser();
            var meeting = repository.saveAndGetOpenMeeting();

            securityContextSetting.set(me.getId());

            api.joinMeeting(VALID_TOKEN, meeting.getId());
            // when
            var result = api.joinMeeting(VALID_TOKEN, meeting.getId());
            // then
            result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
        }

        @Test
        @DisplayName("종료된 모임의 id가 주어진다면, 400 Bad Request를 응답한다.")
        void Return_400_bad_request_if_closed_meeting_id_received() {
            // given
            var me = repository.saveAndGetUser();
            var meeting = repository.saveAndGetCloseMeeting();

            securityContextSetting.set(me.getId());
            // when
            var result = api.joinMeeting(VALID_TOKEN, meeting.getId());
            // then
            result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
        }

        @Test
        @DisplayName("최대 인원이 초과된 모임의 id가 주어지면, 400 Bad Request를 응답한다.")
        void Return_400_bad_request_if_exceed_max_number_of_recruits_meeting_id_received() {
            // given
            var me = repository.saveAndGetUser();
            var meeting = repository.saveAndGetOpenFullMeeting();
            securityContextSetting.set(me.getId());

            // when
            var result = api.joinMeeting(VALID_TOKEN, meeting.getId());
            // then
            result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
        }

        @Test
        @DisplayName("모임 주최자가 모임 참여 취소를 한다면, 400 Bad Request를 응답한다.")
        void Return_400_bad_request_if_host_cancel_meeting() {
            // given
            var host = repository.saveAndGetUser();
            var meeting = repository.saveAndGetOpenMeetingWithHostId(host.getId());
            securityContextSetting.set(host.getId());

            // when
            var result = api.cancelMeeting(VALID_TOKEN, meeting.getId());
            // then
            result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
        }
    }

    @Nested
    @DisplayName("미팅 참여 취소 API는")
    class Cancel_meeting_api {

        @Test
        @DisplayName("존재하는 모임의 id가 주어지면, 모임에 참여를 취소한다.")
        void Cancel_meeting_if_exist_meeting_id_received() {
            // given
            var me = repository.saveAndGetUser();
            var meeting = repository.saveAndGetOpenMeeting();

            securityContextSetting.set(me.getId());
            api.joinMeeting(VALID_TOKEN, meeting.getId());
            // when
            var result = api.cancelMeeting(VALID_TOKEN, meeting.getId());
            // then
            result.expectStatus().isOk();
        }

        @Test
        @DisplayName("참여하지 않은 모임의 id가 주어지면, 400 Bad Request를 응답한다.")
        void Return_400_bad_request_if_not_joined_meeting_id_received() {
            // given
            var me = repository.saveAndGetUser();
            var meeting = repository.saveAndGetOpenMeeting();

            securityContextSetting.set(me.getId());
            // when
            var result = api.cancelMeeting(VALID_TOKEN, meeting.getId());
            // then
            assertThat(result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody()
            )
                .extracting(ErrorResponse::getMessage)
                .isEqualTo("참여하지 않은 모임입니다.");
        }

        @Test
        @DisplayName("종료된 모임의 id가 주어진다면, 400 Bad Request를 응답한다.")
        void Return_400_bad_request_if_closed_meeting_id_received() {
            // given
            var me = repository.saveAndGetUser();
            var meeting = repository.saveAndGetCloseMeeting();

            securityContextSetting.set(me.getId());
            // when
            var result = api.cancelMeeting(VALID_TOKEN, meeting.getId());
            // then
            assertThat(result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody()
            )
                .extracting(ErrorResponse::getMessage)
                .isEqualTo("종료된 모임에서 참여를 취소할 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("북마크 추가 API는")
    class Add_bookmark_api {

        @Test
        @DisplayName("존재하는 모임의 id가 주어지면, 모임을 북마크한다.")
        void Add_bookmark_if_exist_meeting_id_received() {
            // given
            var me = repository.saveAndGetUser();
            var meeting = repository.saveAndGetOpenMeeting();

            securityContextSetting.set(me.getId());
            // when
            var result = api.addBookmark(VALID_TOKEN, meeting.getId());
            // then
            result.expectStatus().isCreated();
        }

        @Test
        @DisplayName("이미 북마크한 모임의 id가 주어지면, 400 Bad Request를 응답한다.")
        void Return_400_bad_request_if_already_bookmarked_meeting_id_received() {
            // given
            var me = repository.saveAndGetUser();
            var meeting = repository.saveAndGetOpenMeeting();

            securityContextSetting.set(me.getId());
            api.addBookmark(VALID_TOKEN, meeting.getId());
            // when
            var result = api.addBookmark(VALID_TOKEN, meeting.getId());
            // then
            result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
        }
    }

    @Nested
    @DisplayName("북마크 취소 API는")
    class Cancel_bookmark_api {

        @Test
        @DisplayName("존재하는 모임의 id가 주어지면, 모임의 북마크를 취소한다.")
        void Cancel_bookmark_if_exist_meeting_id_received() {
            // given
            var me = repository.saveAndGetUser();
            var meeting = repository.saveAndGetOpenMeeting();

            securityContextSetting.set(me.getId());
            api.addBookmark(VALID_TOKEN, meeting.getId());
            // when
            var result = api.cancelBookmark(VALID_TOKEN, meeting.getId());
            // then
            result.expectStatus().isOk();
        }

        @Test
        @DisplayName("북마크하지 않은 모임의 id가 주어지면, 400 Bad Request를 응답한다.")
        void Return_400_bad_request_if_not_bookmarked_meeting_id_received() {
            // given
            var me = repository.saveAndGetUser();
            var meeting = repository.saveAndGetOpenMeeting();

            securityContextSetting.set(me.getId());
            // when
            var result = api.cancelBookmark(VALID_TOKEN, meeting.getId());
            // then
            result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class);
        }
    }

    @Nested
    @DisplayName("미팅 참가자 조회 API는")
    class Get_meeting_participants_api {

        @Test
        @DisplayName("참여한 meeting id 가 주어지면, 참여한 참가자들의 정보가 주어진다.")
        void Get_participants_if_exist_meeting_id_received() {
            // given
            var me = repository.saveAndGetUser();
            var meeting = repository.saveAndGetClosedMetingWithParticipantUserIds(List.of(me.getId(), 2L));

            securityContextSetting.set(me.getId());

            // when
            var result = api.getMeetingParticipants(VALID_TOKEN, meeting.getId());

            // then
            result.expectStatus().isOk();
        }

        @Test
        @DisplayName("API 호출한 회원이 모임에 참여하지 않았다면, 400 bad request 을 응답한다.")
        void Return_400_bad_request_if_meeting_not_contain_user() {
            // given
            var me = repository.saveAndGetUser();
            var meeting = repository.saveAndGetClosedMetingWithParticipantUserIds(List.of(100L, 101L));

            securityContextSetting.set(me.getId());

            // when
            var result = api.getMeetingParticipants(VALID_TOKEN, meeting.getId());

            // then
            assertThat(result.expectStatus().isBadRequest()
                .expectBody(ErrorResponse.class)
                .returnResult()
                .getResponseBody())
                .extracting(ErrorResponse::getMessage)
                .isEqualTo("모임에 참여하지 않은 회원입니다.");
        }
    }
}
