package net.teumteum.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.teumteum.core.entity.TimeBaseEntity;
import net.teumteum.core.security.Authenticated;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

@Getter
@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User extends TimeBaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 10)
    private String name;

    @Column(name = "birth", length = 10)
    @DateTimeFormat(pattern = "yyyy.MM.dd")
    private String birth;

    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "manner_temperature")
    private int mannerTemperature;

    @Embedded
    private OAuth oauth;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type")
    private RoleType roleType;

    @Embedded
    private ActivityArea activityArea;

    @Column(name = "mbti", length = 4)
    private String mbti;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Column(name = "goal", length = 50)
    private String goal;

    @Embedded
    private Job job;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> interests = new ArrayList<>();

    @Embedded
    private Terms terms;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<Long> friends = new HashSet<>();

    public User(Long id, String oauthId, Authenticated authenticated) {
        this.id = id;
        this.oauth = new OAuth(oauthId, authenticated);
    }

    @PrePersist
    private void assertField() {
        assertName();
        assertMannerTemperature();
    }

    public void update(User updatedUser) {
        this.name = updatedUser.name;
        this.birth = updatedUser.birth;
        this.characterId = updatedUser.characterId;
        this.activityArea = updatedUser.activityArea;
        this.mbti = updatedUser.mbti;
        this.status = updatedUser.status;
        this.goal = updatedUser.goal;
        this.job = updatedUser.job;
        this.interests = updatedUser.interests;
        assertName();
        assertMannerTemperature();
    }

    private void assertName() {
        Assert.doesNotContain(name, " ", () -> "이름에 공백이 포함되어 있습니다. \"" + name + "\"");
        Assert.isTrue(name.length() >= 2 && name.length() <= 10, () -> "이름은 2자 ~ 10자 사이가 되어야 합니다. \"" + name + "\"");
    }

    private void assertMannerTemperature() {
        Assert.isTrue(mannerTemperature >= 0, () -> "매너 온도는 0도 이상 이여야 합니다. \"" + mannerTemperature + "\"");
    }

    public void addFriend(User user) {
        friends.add(user.id);
    }

}
