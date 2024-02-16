package net.teumteum.unit.meeting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Optional;
import net.teumteum.meeting.domain.ImageUpload;
import net.teumteum.meeting.domain.MeetingFixture;
import net.teumteum.meeting.domain.MeetingRepository;
import net.teumteum.meeting.domain.response.MeetingParticipantResponse;
import net.teumteum.meeting.service.MeetingService;
import net.teumteum.user.domain.UserConnector;
import net.teumteum.user.domain.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("모임 서비스 단위 테스트의")
public class MeetingServiceTest {

    @InjectMocks
    MeetingService meetingService;

    @Mock
    MeetingRepository meetingRepository;

    @Mock
    UserConnector userConnector;

    @Mock
    ImageUpload imageUpload;

    @Nested
    @DisplayName("모임 참여자 조회 API는")
    class Return_meeting_participants_api_unit {

        @Test
        @DisplayName("API 호출 회원을 제외한, meetingId 해당하는 모임의 참여자 정보를 반환한다.")
        void Return_meeting_participants_with_200_ok() {
            // given
            var userId = 1L;
            var meetingId = 1L;

            var existMeeting
                = MeetingFixture.getCloseMeetingWithParticipantUserIds(List.of(userId, 2L, 3L));

            var existUser2 = UserFixture.getUserWithId(2L);
            var existUser3 = UserFixture.getUserWithId(3L);

            given(meetingRepository.findById(anyLong()))
                .willReturn(Optional.of(existMeeting));

            given(userConnector.findUserById(existUser2.getId())).willReturn(Optional.of(existUser2));
            given(userConnector.findUserById(existUser3.getId())).willReturn(Optional.of(existUser3));

            // when
            List<MeetingParticipantResponse> participants = meetingService.getParticipants(meetingId, userId);
            // then
            assertThat(participants).hasSize(2);
        }

        @Test
        @DisplayName("모임에 API 호출 회원이 존재하지 않는 경우, 400 bad request를 응답한다.")
        void Return_400_bad_request_if_meeting_not_contain_user() {
            // given
            var userId = 1L;
            var notContainedUserId = 4L;

            var meetingId = 1L;

            var existMeeting
                = MeetingFixture.getCloseMeetingWithParticipantUserIds(List.of(userId, 2L, 3L));

            given(meetingRepository.findById(anyLong()))
                .willReturn(Optional.of(existMeeting));

            // when
            assertThatThrownBy(() -> meetingService.getParticipants(meetingId, notContainedUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("모임에 참여하지 않은 회원입니다.");
        }
    }
}
