package net.teumteum.meeting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PromiseDateTime {

    @Column(name = "promise_date")
    private LocalDate date;

    @Column(name = "promise_time")
    private LocalTime time;
}
