package net.teumteum.integration;

import lombok.RequiredArgsConstructor;
import net.teumteum.meeting.domain.Meeting;
import net.teumteum.meeting.domain.MeetingFixture;
import net.teumteum.meeting.domain.MeetingRepository;
import net.teumteum.meeting.domain.Topic;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserFixture;
import net.teumteum.user.domain.UserRepository;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;
import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
class Repository {
    private final UserRepository userRepository;

    private final MeetingRepository meetingRepository;

    User saveAndGetUser() {
        var user = UserFixture.getNullIdUser();
        return userRepository.saveAndFlush(user);
    }

    Meeting saveAndGetOpenMeeting() {
        var meeting = MeetingFixture.getOpenMeeting();
        return meetingRepository.saveAndFlush(meeting);
    }

    Meeting saveAndGetCloseMeeting() {
        var meeting = MeetingFixture.getCloseMeeting();
        return meetingRepository.saveAndFlush(meeting);
    }

    Meeting saveAndGetOpenFullMeeting() {
        var meeting = MeetingFixture.getOpenFullMeeting();
        return meetingRepository.saveAndFlush(meeting);
    }

    List<Meeting> saveAndGetOpenMeetingsByTopic(int size, Topic topic) {
        var meetings = Stream.generate(() -> MeetingFixture.getOpenMeetingWithTopic(topic))
            .limit(size)
            .toList();
        return meetingRepository.saveAllAndFlush(meetings);
    }

    List<Meeting> saveAndGetCloseMeetingsTopic(int size, Topic topic) {
        var meetings = Stream.generate(() -> MeetingFixture.getCloseMeetingWithTopic(topic))
            .limit(size)
            .toList();
        return meetingRepository.saveAllAndFlush(meetings);
    }

    List<Meeting> saveAndGetOpenMeetingsByTitle(int size, String title) {
        var meetings = Stream.generate(() -> MeetingFixture.getOpenMeetingWithTitle(title))
            .limit(size)
            .toList();
        return meetingRepository.saveAllAndFlush(meetings);
    }

    List<Meeting> saveAndGetCloseMeetingsByTitle(int size, String title) {
        var meetings = Stream.generate(() -> MeetingFixture.getCloseMeetingWithTitle(title))
            .limit(size)
            .toList();
        return meetingRepository.saveAllAndFlush(meetings);
    }

    List<Meeting> saveAndGetOpenMeetingsByIntroduction(int size, String introduction) {
        var meetings = Stream.generate(() -> MeetingFixture.getOpenMeetingWithIntroduction(introduction))
            .limit(size)
            .toList();
        return meetingRepository.saveAllAndFlush(meetings);
    }

    List<Meeting> saveAndGetCloseMeetingsByIntroduction(int size, String introduction) {
        var meetings = Stream.generate(() -> MeetingFixture.getCloseMeetingWithIntroduction(introduction))
            .limit(size)
            .toList();
        return meetingRepository.saveAllAndFlush(meetings);
    }

    List<Meeting> saveAndGetOpenMeetingsByParticipantUserId(int size, Long participantUserId) {
        var meetings = Stream.generate(() -> MeetingFixture.getOpenMeetingWithParticipantUserId(participantUserId))
            .limit(size)
            .toList();
        return meetingRepository.saveAllAndFlush(meetings);
    }

    List<Meeting> saveAndGetCloseMeetingsByParticipantUserId(int size, Long participantUserId) {
        var meetings = Stream.generate(() -> MeetingFixture.getCloseMeetingWithParticipantUserId(participantUserId))
            .limit(size)
            .toList();

        return meetingRepository.saveAllAndFlush(meetings);
    }

    List<Meeting> saveAndGetOpenMeetings(int size) {
        var meetings = Stream.generate(MeetingFixture::getOpenMeeting)
            .limit(size)
            .toList();
        return meetingRepository.saveAllAndFlush(meetings);
    }

    void clear() {
        userRepository.deleteAll();
        meetingRepository.deleteAll();
    }
}
