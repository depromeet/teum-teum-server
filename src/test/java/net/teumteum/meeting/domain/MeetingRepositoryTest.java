package net.teumteum.meeting.domain;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.teumteum.core.config.AppConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
@Import(AppConfig.class)
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
    @DisplayName("delete 메소드는")
    class Delete_method {

        @Test
        @DisplayName("모임을 삭제한 유저가 모임의 주최자이면 (hostId = userId), 모임 삭제에 성공한다.")
        void Delete_success_if_exists_meeting_input() {
            // given
            var existsMeeting = MeetingFixture.getDefaultMeeting();

            meetingRepository.saveAndFlush(existsMeeting);
            entityManager.clear();

            // when
            meetingRepository.delete(existsMeeting);
            entityManager.flush();
            entityManager.clear();

            // then
            var result = meetingRepository.findById(existsMeeting.getId());
            Assertions.assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("JPA Specification을 이용한 findAll 메소드 중")
    class FindAllWithSpecificationAndPageNation_method {

        @Test
        @DisplayName("저장된 모임들을 주어진 topic을 가진 모임을 페이지 네이션을 적용해 최신순으로 조회하면, 모임들을 반환한다.")
        void Find_success_if_exists_meetings_topic_and_page_nation_input() {
            // given
            var createSize = 3;
            var expectedMeetings = Stream.generate(() -> MeetingFixture.getOpenMeetingWithTopic(Topic.스터디))
                .limit(createSize)
                .toList();

            meetingRepository.saveAllAndFlush(expectedMeetings);
            entityManager.clear();

            var requestPageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
            var requestTopic = Topic.스터디;

            // when
            var spec = MeetingSpecification.withIsOpen(true).and(MeetingSpecification.withTopic(requestTopic));
            var result = meetingRepository.findAll(spec, requestPageable);

            // then
            Assertions.assertThat(result.getContent())
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(
                    expectedMeetings.stream()
                        .sorted(Comparator.comparing(Meeting::getId).reversed())
                        .toList()
                );
        }

        @Test
        @DisplayName("저장된 모임들을 주어진 참여자가 존재하는 모임을 페이지 네이션을 적용해 최신순으로 조회하면, 모임들을 반환한다.")
        void Find_success_if_exists_meetings_participant_user_id_and_page_nation_input() {
            // given
            var createSize = 3;
            var expectedMeetings = Stream.generate(() -> MeetingFixture.getOpenMeetingWithParticipantUserId(2L))
                .limit(createSize)
                .toList();

            meetingRepository.saveAllAndFlush(expectedMeetings);
            entityManager.clear();

            var requestPageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
            var requestParticipantUserId = 2L;

            // when
            var spec = MeetingSpecification.withIsOpen(true)
                .and(MeetingSpecification.withParticipantUserId(requestParticipantUserId));
            var result = meetingRepository.findAll(spec, requestPageable);

            // then
            Assertions.assertThat(result.getContent())
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(
                    expectedMeetings.stream()
                        .sorted(Comparator.comparing(Meeting::getId).reversed())
                        .toList()
                );
        }

        @Test
        @DisplayName("저장된 모임들을 주어진 모임 장소인 모임을 페이지 네이션을 적용해 최신순으로 조회하면, 모임들을 반환한다.")
        void Find_success_if_exists_meetings_meeting_street_and_page_nation_input() {
            // given
            var createSize = 3;

            var expectedMeetings = Stream.generate(() -> MeetingFixture.getOpenMeetingWithMainStreet("강남"))
                .limit(createSize)
                .toList();

            var existsWrongMeetings = Stream.generate(() -> MeetingFixture.getOpenMeetingWithMainStreet("판교"))
                .limit(createSize)
                .toList();

            meetingRepository.saveAll(expectedMeetings);
            meetingRepository.saveAllAndFlush(existsWrongMeetings);
            entityManager.clear();

            var requestPageable = PageRequest.of(0, 5, Sort.Direction.DESC, "id");
            var requestStreet = "강남";

            // when
            var spec = MeetingSpecification.withIsOpen(true).and(MeetingSpecification.withAreaStreet(requestStreet));
            var result = meetingRepository.findAll(spec, requestPageable);

            // then
            Assertions.assertThat(result.getContent())
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(
                    expectedMeetings.stream()
                        .sorted(Comparator.comparing(Meeting::getId).reversed())
                        .toList()
                );
        }

        @Test
        @DisplayName("저장된 모임들을 주어진 검색어가 제목 또는 설명글에 포함된 모임을 페이지 네이션을 적용해 최신순으로 조회하면, 모임들을 반환한다.")
        void Find_success_if_exists_meetings_search_word_page_nation_input() {
            // given
            var createSize = 3;

            var existsCloseMeetings = Stream.generate(() -> MeetingFixture.getCloseMeetingWithTitle("공부팟 모집"))
                .limit(createSize)
                .toList();

            var existsMeetingsWithTitle = Stream.generate(() -> MeetingFixture.getOpenMeetingWithTitle("공부팟 모집"))
                .limit(createSize)
                .toList();

            var existsMeetingsWithIntroduction = Stream.generate(
                    () -> MeetingFixture.getOpenMeetingWithIntroduction("공부하는 모임입니다."))
                .limit(createSize)
                .toList();

            meetingRepository.saveAll(existsCloseMeetings);
            meetingRepository.saveAll(existsMeetingsWithTitle);
            meetingRepository.saveAllAndFlush(existsMeetingsWithIntroduction);
            entityManager.clear();

            var requestPageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
            var requestSearchWord = "공부";

            // when
            var spec = MeetingSpecification.withSearchWordInTitle(requestSearchWord)
                .or(MeetingSpecification.withSearchWordInIntroduction(requestSearchWord))
                .and(MeetingSpecification.withIsOpen(true));

            var result = meetingRepository.findAll(spec, requestPageable);

            // then
            Assertions.assertThat(result.getContent())
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(
                    Stream.of(existsMeetingsWithTitle, existsMeetingsWithIntroduction)
                        .flatMap(Collection::stream)
                        .sorted(Comparator.comparing(Meeting::getId).reversed())
                        .toList()
                );
        }
    }

    @Nested
    @DisplayName("findAlertMeetings 메소드는")
    class FindUserAlertMeetings_method {

        @Test
        @DisplayName("startTime과 endTime 사이에 있는 Meeting 들을 반환한다.")
        void Return_meetings_between_start_time_and_end_time() {
            // given
            var current = LocalDateTime.now();

            var notAlertMeeting = MeetingFixture.getMeetingWithPromiseDate(current.minusMinutes(1));
            var alertMeeting = MeetingFixture.getMeetingWithPromiseDate(current);
            var notMeeting2 = MeetingFixture.getMeetingWithPromiseDate(current.plusMinutes(1));

            meetingRepository.saveAllAndFlush(List.of(notAlertMeeting, alertMeeting, notMeeting2));

            var expected = List.of(alertMeeting);

            // when
            var result = meetingRepository.findAlertMeetings(current, current.plusMinutes(1));

            // then
            Assertions.assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("value.createdAt", "value.updatedAt", "value.id", "value.promiseDateTime")
                .isEqualTo(expected);
        }
    }
}
