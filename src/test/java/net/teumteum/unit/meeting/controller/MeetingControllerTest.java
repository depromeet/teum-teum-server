package net.teumteum.unit.meeting.controller;

import static net.teumteum.unit.common.SecurityValue.VALID_ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import net.teumteum.core.security.SecurityConfig;
import net.teumteum.core.security.filter.JwtAuthenticationFilter;
import net.teumteum.core.security.service.JwtService;
import net.teumteum.core.security.service.RedisService;
import net.teumteum.core.security.service.SecurityService;
import net.teumteum.meeting.controller.MeetingController;
import net.teumteum.meeting.domain.response.MeetingParticipantResponse;
import net.teumteum.meeting.service.MeetingService;
import net.teumteum.user.domain.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = MeetingController.class,
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = RedisService.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtService.class)}
)
@WithMockUser
@DisplayName("모임 컨트롤러 단위 테스트의")
public class MeetingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeetingService meetingService;

    @MockBean
    private SecurityService securityService;

    @Nested
    @DisplayName("모임 참여자 조회 API는")
    class Get_meeting_participants_api_unit {

        @Test
        @DisplayName("API 호출 회원을 제외한, meetingId 해당하는 모임의 참여자 정보를 반환한다.")
        void Return_meeting_participants_with_200_ok() throws Exception {
            // given
            var existUser1 = UserFixture.getUserWithId(1L);
            var existUser2 = UserFixture.getUserWithId(2L);
            var existUser3 = UserFixture.getUserWithId(3L);

            List<MeetingParticipantResponse> response
                = List.of(MeetingParticipantResponse.of(existUser2), MeetingParticipantResponse.of(existUser3));

            given(securityService.getCurrentUserId())
                .willReturn(existUser1.getId());

            given(meetingService.getParticipants(anyLong(), anyLong()))
                .willReturn(response);

            // when && then
            mockMvc.perform(get("/meetings/{meetingId}/participants", 2L)
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].characterId").value(1));
        }

        @Test
        @DisplayName("모임에 API 호출 회원이 존재하지 않는 경우, 400 bad request를 응답한다.")
        void Return_400_bad_request_if_meeting_not_contain_user() throws Exception {
            // given
            var existUser1 = UserFixture.getUserWithId(1L);

            given(securityService.getCurrentUserId())
                .willReturn(existUser1.getId());

            given(meetingService.getParticipants(anyLong(), anyLong())).willThrow(
                new IllegalArgumentException("모임에 참여하지 않은 회원입니다."));

            // when & then
            mockMvc.perform(get("/meetings/{meetingId}/participants", 2L)
                    .with(csrf())
                    .header(AUTHORIZATION, VALID_ACCESS_TOKEN))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("모임에 참여하지 않은 회원입니다."));
        }
    }
}
