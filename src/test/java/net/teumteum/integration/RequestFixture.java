package net.teumteum.integration;

import net.teumteum.user.domain.User;
import net.teumteum.user.domain.request.UserUpdateRequest;
import net.teumteum.user.domain.request.UserUpdateRequest.NewActivityArea;
import net.teumteum.user.domain.request.UserUpdateRequest.NewJob;

public class RequestFixture {

    public static UserUpdateRequest userUpdateRequest(User user) {
        return new UserUpdateRequest(
            user.getId(),
            "new_name",
            user.getBirth(),
            user.getCharacterId(),
            newActivityArea(user),
            user.getMbti(),
            user.getStatus().name(),
            user.getGoal(),
            newJob(user),
            user.getInterests()
        );
    }

    private static NewActivityArea newActivityArea(User user) {
        return new NewActivityArea(
            user.getActivityArea().getCity(),
            user.getActivityArea().getStreet()
        );
    }

    private static NewJob newJob(User user) {
        return new NewJob(
            user.getJob().getName(),
            user.getJob().getJobClass(),
            user.getJob().getDetailJobClass()
        );
    }

}
