package net.teumteum.meeting.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.teumteum.core.entity.TimeBaseEntity;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Meeting extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", length = 32)
    private String title;

    @Column(name = "host_user_id")
    private Long hostUserId;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Long> participantUserIds = new HashSet<>();

    @Column(name = "topic")
    @Enumerated(EnumType.STRING)
    private Topic topic;

    @Column(name = "introduction", length = 200)
    private String introduction;

    @Embedded
    private MeetingArea meetingArea;

    @Column(name = "number_of_recruits")
    private int numberOfRecruits;

    @Column(name = "promise_date_time")
    private LocalDateTime promiseDateTime;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> imageUrls = new HashSet<>();

    public void addParticipant(Long userId) {
        assertParticipantUserIds();
        participantUserIds.add(userId);
    }

    public boolean alreadyParticipant(Long userId) {
        return participantUserIds.contains(userId);
    }

    public boolean isOpen() {
        return promiseDateTime.isAfter(LocalDateTime.now());
    }

    @PrePersist
    private void assertField() {
        assertTitle();
        assertNumberOfRecruits();
        assertIntroduction();
    }

    private void assertIntroduction() {
        Assert.isTrue(introduction.length() >= 10 && introduction.length() <= 200, "모임 소개는 10자 ~ 200자 사이가 되어야 합니다. [현재 입력된 모임 소개] : " + introduction);
    }

    private void assertNumberOfRecruits() {
        Assert.isTrue(numberOfRecruits >= 2 && numberOfRecruits <= 6, "참여자 수는 2명 ~ 6명 사이가 되어야 합니다. [현재 입력된 참여자 수] : " + numberOfRecruits);
    }

    private void assertTitle() {
        Assert.isTrue(title.length() >= 2 && title.length() <= 32, "모임 제목은 2자 ~ 32자 사이가 되어야 합니다. [현재 입력된 모임 제목] : " + title);
    }

    private void assertParticipantUserIds() {
        Assert.isTrue(participantUserIds.size() + 1 <= numberOfRecruits, "최대 참여자 수에 도달한 모임에 참여할 수 없습니다." + "[최대 참여자 수] : " + numberOfRecruits + "[현재 참여자 수] : " + participantUserIds.size());
    }

}
