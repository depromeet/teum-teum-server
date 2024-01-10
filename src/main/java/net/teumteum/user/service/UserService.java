package net.teumteum.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.teumteum.user.domain.InterestQuestion;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserRepository;
import net.teumteum.user.domain.request.UserUpdateRequest;
import net.teumteum.user.domain.response.FriendsResponse;
import net.teumteum.user.domain.response.InterestQuestionResponse;
import net.teumteum.user.domain.response.UserGetResponse;
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

    public UserGetResponse getUserById(Long userId) {
        var existUser = getUser(userId);

        return UserGetResponse.of(existUser);
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

    public FriendsResponse findFriendsByUserId(Long userId) {
        var user = getUser(userId);
        var friends = userRepository.findAllById(user.getFriends());

        return FriendsResponse.of(friends);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("userId에 해당하는 user를 찾을 수 없습니다. \"" + userId + "\""));
    }

    public InterestQuestionResponse getInterestQuestionByUserIds(List<Long> userIds) {
        var users = userRepository.findAllById(userIds);
        Assert.isTrue(users.size() >= 2,
            () -> {
                throw new IllegalArgumentException("userIds는 2개 이상 주어져야 합니다.");
            }
        );

        return interestQuestion.getQuestion(users);
    }
}
