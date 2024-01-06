package net.teumteum.user.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserConnector;
import net.teumteum.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserConnectorImpl implements UserConnector {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

}
