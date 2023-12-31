package net.teumteum.meeting.domain;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

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
            var existsMeeting = MeetingFixture.getDefaultMeeting();

            meetingRepository.saveAndFlush(existsMeeting);
            entityManager.clear();

            // when
            var result = meetingRepository.findById(existsMeeting.getId());

            // then
            Assertions.assertThat(result)
                    .isPresent()
                    .usingRecursiveComparison()
                    .ignoringFields("value.createdAt", "value.updatedAt")
                    .isEqualTo(Optional.of(existsMeeting));
        }
    }

    @Nested
    @DisplayName("findByPromiseDateTimeGreaterThanOrderByIdDesc 메소드는")
    class FindAllByOrderByIdDesc_method {

        @Test
        @DisplayName("최신순으로 저장된 열린 모임들을 size 만큼 반환한다.")
        void FindSizeOrderByDesc_success_if_exists_meeting_id_input() {
            // given
            var createSize = 5;
            var findSize = 3;
            var existsMeetings = Stream.generate(MeetingFixture::getOpenMeeting)
                    .limit(createSize)
                    .toList();

            meetingRepository.saveAllAndFlush(existsMeetings);
            entityManager.clear();

            // when
            var result = meetingRepository.findByPromiseDateTimeGreaterThanOrderByIdDesc(
                    PageRequest.of(0, findSize),
                    LocalDateTime.now());

            // then
            Assertions.assertThat(result)
                    .hasSize(findSize);
            Assertions.assertThat(result.stream().map(Meeting::getId))
                    .containsExactlyElementsOf(existsMeetings.stream()
                            .map(Meeting::getId)
                            .toList()
                            .reversed()
                            .subList(0, findSize));
        }
    }

    @Nested
    @DisplayName("findByIdLessThanEqualAndPromiseDateTimeGreaterThanOrderByIdDesc 메소드는")
    class FindByIdLessThanEqualOrderByIdDesc_method {

        @Test
        @DisplayName("cursorId 이하의 최신순으로 저장된 열린 모임들을 size 만큼 반환한다.")
        void FindSizeOrderByDesc_success_if_exists_meeting_id_input() {
            // given
            var createSize = 5;
            var cursorId = 4L;
            var findSize = 3;


            var existsMeetings = Stream.generate(MeetingFixture::getOpenMeeting)
                    .limit(createSize)
                    .toList();

            meetingRepository.saveAllAndFlush(existsMeetings);
            entityManager.clear();

            // when
            var result = meetingRepository.findByIdLessThanEqualAndPromiseDateTimeGreaterThanOrderByIdDesc(
                    cursorId,
                    PageRequest.of(0, findSize),
                    LocalDateTime.now()
            );

            // then
            Assertions.assertThat(result)
                    .hasSize(findSize);
        }
    }

}
