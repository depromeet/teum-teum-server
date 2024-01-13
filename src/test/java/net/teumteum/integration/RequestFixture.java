package net.teumteum.integration;

import static net.teumteum.core.security.Authenticated.네이버;

import net.teumteum.user.domain.Job;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.request.UserRegisterRequest;
import net.teumteum.user.domain.request.UserRegisterRequest.ActivityArea;
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

    public static UserRegisterRequest userRegisterRequest(User user) {
        return new UserRegisterRequest(
            user.getOauth().getOauthId(),
            "name",
            user.getBirth(),
            user.getCharacterId(),
            네이버,
            activityArea(user),
            user.getMbti(),
            user.getStatus().name(),
            null,
            user.getInterests(),
            user.getGoal()
        );
    }

    private static ActivityArea activityArea(User user) {
        return new ActivityArea(
            user.getActivityArea().getCity(),
            user.getActivityArea().getStreet()
        );
    }

    private static Job job(User user) {
        return new Job(
            user.getJob().getName(),
            false,
            user.getJob().getJobClass(),
            user.getJob().getDetailJobClass()
        );
    }

}
