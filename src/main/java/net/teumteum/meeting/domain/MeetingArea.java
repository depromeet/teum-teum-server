package net.teumteum.meeting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

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
        return new MeetingArea(toMainStreet(roadName), roadName, addressDetail);
    }

    private static String toMainStreet(String roadName) {
        String[] roadNameSplit = roadName.split(" ");
        Assert.isTrue(roadNameSplit.length >= 2, "잘못된 도로명 주소입니다. \"" + roadName + "\"");
        return roadNameSplit[0] + " " + roadNameSplit[1];
    }

}
