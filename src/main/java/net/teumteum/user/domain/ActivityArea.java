package net.teumteum.user.domain;


import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ActivityArea {

    @Column(name = "city")
    private String city;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> street = new ArrayList<>();
}
