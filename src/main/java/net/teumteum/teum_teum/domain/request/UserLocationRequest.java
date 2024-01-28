package net.teumteum.teum_teum.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.teumteum.teum_teum.domain.UserLocation;

public record UserLocationRequest(
    @NotNull(message = "id 는 필수 입력값입니다.")
    Long id,
    @NotNull(message = "위도는 필수 입력값입니다.")
    Double latitude,
    @NotNull(message = "경도는 필수 입력값입니다.")
    Double longitude,
    @NotBlank(message = "이름은 필수 입력값입니다.")
    String name,
    @NotBlank(message = "직무는 필수 입력값입니다.")
    String jobDetailClass,
    @NotNull(message = "캐릭터 id 는 필수 입력값입니다.")
    Long characterId
) {

    public UserLocation toUserLocation() {
        return new UserLocation(id, latitude, longitude, name, jobDetailClass, characterId);
    }
}
