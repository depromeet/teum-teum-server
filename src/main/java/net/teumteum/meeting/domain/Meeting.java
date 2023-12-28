package net.teumteum.meeting.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.teumteum.core.entity.TimeBaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Meeting extends TimeBaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

}
