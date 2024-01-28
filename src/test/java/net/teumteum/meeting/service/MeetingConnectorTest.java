package net.teumteum.meeting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import net.teumteum.meeting.domain.MeetingConnector;
import net.teumteum.meeting.domain.MeetingFixture;
import net.teumteum.meeting.domain.MeetingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DisplayName("MeetingConnector 클래스의")
@ContextConfiguration(classes = MeetingConnectorImpl.class)
public class MeetingConnectorTest {

    private static final Long EXIST_MEETING_ID = 1L;

    @Autowired
    private MeetingConnector meetingConnector;

    @MockBean
    private MeetingRepository meetingRepository;

    @BeforeEach
    void beforeEach() {
        var defaultMeeting = MeetingFixture.getDefaultMeeting();
        given(meetingRepository.existsById(anyLong())).willReturn(true);
        given(meetingRepository.findById(anyLong())).willReturn(Optional.of(defaultMeeting));
    }

    @Nested
    @DisplayName("existsById 메소드는")
    class ExistsById_Method {

        @Test
        @DisplayName("존재하는 meeting의 id가 들어오면, true을 반환한다.")
        void Return_true_if_exists_meeting_id() {
            // given
            var expect = true;

            // when
            var result = meetingConnector.existById(EXIST_MEETING_ID);

            // then
            assertThat(result)
                .isTrue();
        }
    }

    @Nested
    @DisplayName("findById 메소드는")
    class FindById_Method {

        @Test
        @DisplayName("존재하는 meeting의 id가 들어오면, optional.meeting을 한환한다.")
        void Return_optional_meeting_if_exists_meeting_id() {
            // given
            var expect = MeetingFixture.getDefaultMeeting();

            // when
            var result = meetingConnector.findById(EXIST_MEETING_ID);

            // then
            assertThat(result.get().getTitle()).isEqualTo(expect.getTitle());
            assertThat(result.get().getId()).isEqualTo(expect.getId());
        }
    }
}
