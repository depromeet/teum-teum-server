package net.teumteum.integration;

import java.util.UUID;
import net.teumteum.core.security.Authenticated;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.request.UserRegisterRequest;
import net.teumteum.user.domain.request.UserRegisterRequest.Job;
import net.teumteum.user.domain.request.UserRegisterRequest.Terms;
import net.teumteum.user.domain.request.UserUpdateRequest;
import net.teumteum.user.domain.request.UserUpdateRequest.NewJob;

public class RequestFixture {

    public static UserUpdateRequest userUpdateRequest(User user) {
        return new UserUpdateRequest(user.getId(), "new_name", user.getBirth(), user.getCharacterId(),
            user.getActivityArea(), user.getMbti(), user.getStatus().name(), user.getGoal(), newJob(user),
            user.getInterests());
    }

    private static NewJob newJob(User user) {
        return new NewJob(user.getJob().getName(), user.getJob().getJobClass(), user.getJob().getDetailJobClass());
    }


    public static UserRegisterRequest userRegisterRequest(User user) {
        return new UserRegisterRequest(UUID.randomUUID().toString(),
            terms(user), user.getName(),
            user.getBirth(), user.getCharacterId(), Authenticated.카카오, user.getActivityArea(),
            user.getMbti(), user.getStatus().name(), job(user),
            user.getInterests(), user.getGoal());
    }

    public static UserRegisterRequest userRegisterRequestWithFail(User user) {
        return new UserRegisterRequest(user.getOauth().getOauthId(),
            terms(user), user.getName(),
            user.getBirth(), user.getCharacterId(), user.getOauth().getAuthenticated(), user.getActivityArea(),
            user.getMbti(), user.getStatus().name(), job(user),
            user.getInterests(), user.getGoal());
    }

    public static UserRegisterRequest userRegisterRequestWithNoValid(User user) {
        return new UserRegisterRequest(user.getOauth().getOauthId(),
            terms(user), null,
            user.getBirth(), user.getCharacterId(), user.getOauth().getAuthenticated(), user.getActivityArea(),
            user.getMbti(), user.getStatus().name(), job(user),
            null, user.getGoal());
    }

    private static Job job(User user) {
        return new Job(user.getJob().getName(),
            user.getJob().getJobClass(),
            user.getJob().getDetailJobClass());
    }

    private static Terms terms(User user) {
        return new Terms(user.getTerms().getService(), user.getTerms().getPrivacyPolicy());
    }
}
