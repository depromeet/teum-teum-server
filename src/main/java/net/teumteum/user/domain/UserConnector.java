package net.teumteum.user.domain;

import net.teumteum.core.security.Authenticated;

import java.util.List;
import java.util.Optional;

public interface UserConnector {

    Optional<User> findUserById(Long id);

    List<User> findAllUser();

    Optional<User> findByAuthenticatedAndOAuthId(Authenticated authenticated, String oAuthId);

}
