package net.teumteum.user.service;

import lombok.RequiredArgsConstructor;
import net.teumteum.core.security.Authenticated;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserConnector;
import net.teumteum.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserConnectorImpl implements UserConnector {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findByAuthenticatedAndOAuthId(Authenticated authenticated, String oAuthId) {
        return userRepository.findByAuthenticatedAndOAuthId(authenticated, oAuthId);
    }
}
