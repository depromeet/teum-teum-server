package net.teumteum.meeting.integration;

import lombok.RequiredArgsConstructor;
import net.teumteum.meeting.domain.Meeting;
import net.teumteum.meeting.domain.MeetingFixture;
import net.teumteum.meeting.domain.MeetingRepository;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;
import java.util.stream.Stream;

@TestComponent
@RequiredArgsConstructor
class Repository {
    private final MeetingRepository meetingRepository;
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
        meetingRepository.deleteAll();
    }
}
