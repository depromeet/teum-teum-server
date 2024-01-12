package net.teumteum.auth.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.core.security.service.JwtService;
import net.teumteum.user.domain.User;
import net.teumteum.user.domain.UserConnector;
import org.springframework.stereotype.Service;

@Slf4j
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
