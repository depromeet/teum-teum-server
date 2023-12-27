package net.teumteum.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Column(name = "job_name")
    private String name;

    @Column(name = "certificated")
    private boolean certificated;

    @Column(name = "job_class")
    private String jobClass;

    @Column(name = "detail_job_class")
    private String detailJobClass;

}
