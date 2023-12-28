package net.teumteum.meeting.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.teumteum.core.entity.TimeBaseEntity;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection
    private List<Long> participantUserIds = new ArrayList<>();

    @Column(name = "topic")
    @Enumerated(EnumType.STRING)
    private Topic topic;

    @Column(name = "introduction", length = 200)
    private String introduction;

    @Embedded
    private MeetingArea meetingArea;

    @Column(name = "number_of_recruits")
    private int numberOfRecruits;

    @Embedded
    private PromiseDateTime promiseDateTime;

    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

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

}
