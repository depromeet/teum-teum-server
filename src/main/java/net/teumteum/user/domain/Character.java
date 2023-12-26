package net.teumteum.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Character {

    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "background_color_id")
    private Long backgroundColorId;

}
