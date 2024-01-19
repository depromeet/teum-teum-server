package net.teumteum.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.teumteum.core.entity.TimeBaseEntity;

@Getter
@AllArgsConstructor
@Entity(name = "withdraw_reasons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WithdrawReason extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "withdraw_reason", nullable = false)
    private String reason;
}
