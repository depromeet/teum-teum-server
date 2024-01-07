package net.teumteum.integration;

import net.teumteum.core.error.ErrorResponse;
import net.teumteum.meeting.domain.Meeting;
import net.teumteum.meeting.domain.Topic;
import net.teumteum.meeting.domain.response.MeetingResponse;
import net.teumteum.meeting.domain.response.MeetingsResponse;
import net.teumteum.meeting.model.PageDto;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Stream;

@DisplayName("미팅 통합테스트의")
class MeetingIntegrationTest extends IntegrationTest {

    public static final int DEFAULT_QUERY_SIZE = 5;
    private static final String VALID_TOKEN = "VALID_TOKEN";
    private static final String INVALID_TOKEN = "IN_VALID_TOKEN";
    public static final Pageable FIRST_PAGE_NATION = PageRequest.of(0, DEFAULT_QUERY_SIZE, Sort.Direction.DESC, "id");

    @Nested
    @DisplayName("단일 미팅 조회 API는")
    class Find_meeting_api {

        @Test
        @DisplayName("존재하는 모임의 id가 주어지면, 모임 정보를 응답한다.")
        void Return_meeting_info_if_exist_meeting_id_received() {
            // given
            var meeting = repository.saveAndGetOpenMeeting();
            var expected = MeetingResponse.of(meeting);
            // when
            var result = api.getMeetingById(VALID_TOKEN, meeting.getId());
            // then
            Assertions.assertThat(
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
            var notExistMeetingId = 1L;
            // when
            var result = api.getMeetingById(VALID_TOKEN, notExistMeetingId);
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
            Assertions.assertThat(
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
            Assertions.assertThat(
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
            Assertions.assertThat(
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
            Assertions.assertThat(
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

            loginContext.setUserId(me.getId());
            // when
            var result = api.joinMeeting(VALID_TOKEN, existMeeting.getId());
            // then
            Assertions.assertThat(
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

            loginContext.setUserId(me.getId());
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
            var meeting = repository.saveAndGetCloseMeeting();
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
            var meeting = repository.saveAndGetOpenFullMeeting();
            // when
            var result = api.joinMeeting(VALID_TOKEN, meeting.getId());
            // then
            result.expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class);
        }
    }
}
