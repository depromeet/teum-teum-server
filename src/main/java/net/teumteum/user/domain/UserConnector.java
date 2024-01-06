package net.teumteum.user.domain;

import java.util.List;
import java.util.Optional;

public interface UserConnector {

    Optional<User> findUserById(Long id);

    List<User> findAllUser();
}
