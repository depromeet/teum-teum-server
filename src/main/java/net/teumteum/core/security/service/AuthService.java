package net.teumteum.core.security.service;

import lombok.RequiredArgsConstructor;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserConnector;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtService jwtService;
    private final UserConnector userConnector;
    public Optional<User> findUserByToken(String accessToken) {
        Long id = Long.parseLong(jwtService.getUserIdFromToken(accessToken));
        return userConnector.findUserById(id);
    }
}
