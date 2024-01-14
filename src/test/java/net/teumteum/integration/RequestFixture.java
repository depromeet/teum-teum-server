package net.teumteum.integration;

import java.util.UUID;
import net.teumteum.core.security.Authenticated;
import net.teumteum.user.domain.Job;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.request.UserRegisterRequest;
import net.teumteum.user.domain.request.UserRegisterRequest.ActivityArea;
import net.teumteum.user.domain.request.UserRegisterRequest.Terms;
import net.teumteum.user.domain.request.UserUpdateRequest;
import net.teumteum.user.domain.request.UserUpdateRequest.NewActivityArea;
import net.teumteum.user.domain.request.UserUpdateRequest.NewJob;

public class RequestFixture {

    public static UserUpdateRequest userUpdateRequest(User user) {
        return new UserUpdateRequest(user.getId(), "new_name", user.getBirth(), user.getCharacterId(),
            newActivityArea(user), user.getMbti(), user.getStatus().name(), user.getGoal(), newJob(user),
            user.getInterests());
    }

    private static NewActivityArea newActivityArea(User user) {
        return new NewActivityArea(user.getActivityArea().getCity(), user.getActivityArea().getStreet());
    }

    private static NewJob newJob(User user) {
        return new NewJob(user.getJob().getName(), user.getJob().getJobClass(), user.getJob().getDetailJobClass());
    }

    public static UserRegisterRequest userRegisterRequest(User user) {
        return new UserRegisterRequest(UUID.randomUUID().toString(),
            new Terms(user.getTerms().getService(), user.getTerms().getPrivacyPolicy()), user.getName(),
            user.getBirth(), user.getCharacterId(), Authenticated.카카오, activityArea(user),
            user.getMbti(), user.getStatus().name(), new UserRegisterRequest.Job("직장인", "디자인", "BX 디자이너"),
            user.getInterests(), user.getGoal());
    }

    public static UserRegisterRequest userRegisterRequestWithFail(User user) {
        return new UserRegisterRequest(user.getOauth().getOauthId(),
            new Terms(user.getTerms().getService(), user.getTerms().getPrivacyPolicy()), user.getName(),
            user.getBirth(), user.getCharacterId(), user.getOauth().getAuthenticated(), activityArea(user),
            user.getMbti(), user.getStatus().name(), new UserRegisterRequest.Job("직장인", "디자인", "BX 디자이너"),
            user.getInterests(), user.getGoal());
    }

    private static ActivityArea activityArea(User user) {
        return new ActivityArea(user.getActivityArea().getCity(), user.getActivityArea().getStreet());
    }

    private static Job job(User user) {
        return new Job(user.getJob().getName(), false, user.getJob().getJobClass(), user.getJob().getDetailJobClass());
    }

}
