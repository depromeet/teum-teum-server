package net.teumteum.user.domain.request;


import jakarta.validation.constraints.Size;
import java.util.List;
import net.teumteum.user.domain.WithdrawReason;

public record UserWithdrawRequest(
    @Size(min = 1, max = 3, message = "탈퇴 사유는 최소 1개, 최대 3개의 입력값입니다.")
    List<String> withdrawReasons
) {

    private static final Long IGNORE_ID = null;

    public List<WithdrawReason> toEntity() {
        return withdrawReasons.stream()
            .map(withdrawReason -> new WithdrawReason(IGNORE_ID, withdrawReason))
            .toList();
    }
}
