package net.teumteum.meeting.domain;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
@DisplayName("MeetingRepository 클래스의")
class MeetingRepositoryTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    @DisplayName("save 메소드는")
    class Save_method {

        @Test
        @DisplayName("올바른 MeetingEntity가 들어오면, 모임 저장에 성공한다.")
        void Save_success_if_correct_meeting_entered() {
            // given
            var newMeeting = MeetingFixture.getDefaultMeeting();

            // when
            var result = Assertions.catchException(() -> meetingRepository.saveAndFlush(newMeeting));

            // then
            Assertions.assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("findById 메소드는")
    class FindById_method {

        @Test
        @DisplayName("저장된 모임의 id로 조회하면, 모임을 반환한다.")
        void Find_success_if_exists_meeting_id_input() {
            // given
            var id = 1L;
            var existsMeeting = MeetingFixture.getMeetingWithId(id);

            meetingRepository.saveAndFlush(existsMeeting);
            entityManager.clear();

            // when
            var result = meetingRepository.findById(id);

            // then
            Assertions.assertThat(result)
                    .isPresent()
                    .usingRecursiveComparison()
                    .ignoringFields("value.createdAt", "value.updatedAt")
                    .isEqualTo(Optional.of(existsMeeting));
        }
    }

}
