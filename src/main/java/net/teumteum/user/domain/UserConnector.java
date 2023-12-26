package net.teumteum.user.domain;

import java.util.Optional;

public interface UserConnector {

    Optional<User> findUserById(Long id);
}
