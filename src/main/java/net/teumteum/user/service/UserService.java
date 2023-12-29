package net.teumteum.user.service;

import lombok.RequiredArgsConstructor;
import net.teumteum.user.domain.UserRepository;
import net.teumteum.user.domain.response.UserGetResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserGetResponse getUserById(Long userId) {
        var existUser = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("userId에 해당하는 user를 찾을 수 없습니다. \"" + userId + "\""));

        return UserGetResponse.of(existUser);
    }

}
