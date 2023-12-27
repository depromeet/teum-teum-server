package net.teumteum.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.teumteum.core.entity.TimeBaseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

@Getter
@Entity(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User extends TimeBaseEntity {

    @Id
    @Column(name = "id")
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
    private Oauth oauth;

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

    @ElementCollection
    private List<String> interests;

    @Embedded
    private Terms terms;

    @PrePersist
    private void assertField() {
        assertName();
    }

    private void assertName() {
        Assert.doesNotContain(name, " ", () -> "이름에 공백이 포함되어 있습니다. \"" + name + "\"");
        Assert.isTrue(name.length() >= 2 && name.length() <= 10, () -> "이름은 2자 ~ 10자 사이가 되어야 합니다. \"" + name + "\"");
    }

}
