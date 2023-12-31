package net.teumteum.meeting.integration;

import net.teumteum.core.error.ErrorResponse;
import net.teumteum.meeting.domain.ResultCursor;
import net.teumteum.meeting.domain.response.MeetingResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;

@DisplayName("유저 통합테스트의")
class MeetingIntegrationTest extends IntegrationTest {
    private static final String VALID_TOKEN = "VALID_TOKEN";
    private static final String INVALID_TOKEN = "IN_VALID_TOKEN";
    public static final Long FIRST_REQUEST_CURSOR_ID = 0L;

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
        @DisplayName("첫 페이지 요청에 주어진 size 만큼 미팅 목록을 최신순으로 응답한다.")
        void Return_meeting_list_if_0_cursor_id_and_size_received() {
            // given
            var existSize = 5;
            var meetings = repository.saveAndGetOpenMeetings(existSize);

            List<MeetingResponse> expectedData = meetings.reversed().stream()
                    .map(MeetingResponse::of)
                    .toList();
            var expected = ResultCursor.of(expectedData, false, null);
            var requestSize = 5;
            // when
            var result = api.getOpenMeetings(VALID_TOKEN, FIRST_REQUEST_CURSOR_ID, requestSize);
            // then
            Assertions.assertThat(
                            result.expectStatus().isOk()
                                    .expectBody(new ParameterizedTypeReference<ResultCursor<MeetingResponse>>() {
                                    })
                                    .returnResult().getResponseBody())
                    .usingRecursiveComparison()
                    .ignoringFields("hasNext", "cursorId")
                    .isEqualTo(expected);
        }

        @Test
        @DisplayName("n번째 페이지 요청에 주어진 size 만큼 미팅 목록을 최신순으로 응답한다.")
        void Return_meeting_list_if_n_cursor_id_and_size_received() {
            // given
            var existSize = 10;
            var meetings = repository.saveAndGetOpenMeetings(existSize);

            var requestSize = 5;
            var expectedData = meetings.reversed().subList(0, requestSize).stream()
                    .map(MeetingResponse::of)
                    .toList();
            var cursorId = expectedData.getFirst().id();
            ResultCursor<MeetingResponse> expected = ResultCursor.of(expectedData, true, cursorId);
            // when
            var result = api.getOpenMeetings(VALID_TOKEN, cursorId, requestSize);
            // then
            Assertions.assertThat(
                            result.expectStatus().isOk()
                                    .expectBody(new ParameterizedTypeReference<ResultCursor<MeetingResponse>>() {
                                    })
                                    .returnResult().getResponseBody())
                    .usingRecursiveComparison()
                    .ignoringFields("hasNext", "cursorId")
                    .isEqualTo(expected);
        }
    }
}
