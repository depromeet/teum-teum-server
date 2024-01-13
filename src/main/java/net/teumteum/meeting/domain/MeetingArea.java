package net.teumteum.meeting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class MeetingArea {

    @Column(name = "main_street")
    private String mainStreet;

    @Column(name = "address")
    private String address;

    @Column(name = "address_detail")
    private String addressDetail;

    public static MeetingArea of(String roadName, String addressDetail) {
        return new MeetingArea(roadName.split(" ")[1], roadName, addressDetail);
    }

}
