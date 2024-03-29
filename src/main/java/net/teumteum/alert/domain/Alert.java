package net.teumteum.alert.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.teumteum.core.entity.TimeBaseEntity;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "alert")
@Entity(name = "alert")
public class Alert extends TimeBaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private AlertType type;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    public void read() {
        isRead = true;
    }
}
