package net.teumteum.integration;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import net.teumteum.meeting.domain.Meeting;
import net.teumteum.meeting.domain.MeetingFixture;
import net.teumteum.meeting.domain.MeetingRepository;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserFixture;
import net.teumteum.user.domain.UserRepository;
import org.springframework.boot.test.context.TestComponent;

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
