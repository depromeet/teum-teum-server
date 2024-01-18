package net.teumteum.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.teumteum.core.security.Authenticated;
import net.teumteum.core.security.service.JwtService;
import net.teumteum.core.security.service.RedisService;
import net.teumteum.core.security.service.SecurityService;
import net.teumteum.user.domain.BalanceGameType;
import net.teumteum.user.domain.InterestQuestion;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserRepository;
import net.teumteum.user.domain.request.UserRegisterRequest;
import net.teumteum.user.domain.request.UserUpdateRequest;
import net.teumteum.user.domain.response.FriendsResponse;
import net.teumteum.user.domain.response.InterestQuestionResponse;
import net.teumteum.user.domain.response.UserGetResponse;
import net.teumteum.user.domain.response.UserMeGetResponse;
import net.teumteum.user.domain.response.UserRegisterResponse;
import net.teumteum.user.domain.response.UsersGetByIdResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final InterestQuestion interestQuestion;
    private final RedisService redisService;
    private final JwtService jwtService;

    public UserGetResponse getUserById(Long userId) {
        var existUser = getUser(userId);

        return UserGetResponse.of(existUser);
    }

    public UserMeGetResponse getMe(Long userId) {
        return UserMeGetResponse.of(getUser(userId));
    }

    public UsersGetByIdResponse getUsersById(List<Long> userIds) {
        var existUsers = userRepository.findAllById(userIds);

        assertIsAllUserExist(userIds, existUsers);

        return UsersGetByIdResponse.of(existUsers);
    }

    private void assertIsAllUserExist(List<Long> userIds, List<User> existUsers) {
        Assert.isTrue(userIds.size() == existUsers.size(),
            () -> "요청한 userId들에 해당하는 User가 모두 존재하지 않습니다.");
    }

    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request) {
        var existUser = getUser(userId);

        existUser.update(request.toUser());
    }

    @Transactional
    public void addFriends(Long myId, Long friendId) {
        var me = getUser(myId);
        var friend = getUser(friendId);

        me.addFriend(friend);
    }

    @Transactional
    public void withdraw(Long userId) {
        var existUser = getUser(userId);

        userRepository.delete(existUser);
        redisService.deleteData(String.valueOf(userId));
    }

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest request) {
        checkUserExistence(request.authenticated(), request.id());
        User savedUser = userRepository.save(request.toUser());

        return UserRegisterResponse.of(savedUser.getId(), jwtService.createServiceToken(savedUser));
    }

    @Transactional
    public void logout(Long userId) {
        redisService.deleteData(String.valueOf(userId));
        SecurityService.clearSecurityContext();
    }


    public FriendsResponse findFriendsByUserId(Long userId) {
        var user = getUser(userId);
        var friends = userRepository.findAllById(user.getFriends());

        return FriendsResponse.of(friends);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("userId에 해당하는 user를 찾을 수 없습니다. \"" + userId + "\""));
    }

    public InterestQuestionResponse getInterestQuestionByUserIds(List<Long> userIds, String type) {
        var users = userRepository.findAllById(userIds);
        Assert.isTrue(users.size() >= 2,
            () -> {
                throw new IllegalArgumentException("userIds는 2개 이상 주어져야 합니다.");
            }
        );

        return BalanceGameType.of(type).getInterestQuestionResponse(users, interestQuestion);
    }

    private void checkUserExistence(Authenticated authenticated, String oauthId) {
        userRepository.findByAuthenticatedAndOAuthId(authenticated, oauthId)
            .ifPresent(user -> {
                throw new IllegalArgumentException("일치하는 user 가 이미 존재합니다.");
            });
    }
}
