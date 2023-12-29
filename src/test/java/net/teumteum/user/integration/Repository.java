package net.teumteum.user.integration;

import lombok.RequiredArgsConstructor;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserFixture;
import net.teumteum.user.domain.UserRepository;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
class Repository {

    private final UserRepository userRepository;

    User saveAndGetUser() {
        var user = UserFixture.getNullIdUser();
        return userRepository.saveAndFlush(user);
    }

    void clear() {
        userRepository.deleteAll();
    }

}
