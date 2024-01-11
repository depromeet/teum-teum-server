package net.teumteum.user.domain;

import net.teumteum.core.security.Authenticated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from users u " +
            "where u.oauth.authenticated  = :authenticated and u.oauth.oauthId = :oAuthId")
    Optional<User> findByAuthenticatedAndOAuthId(@Param("authenticated") Authenticated authenticated,
                                                 @Param("oAuthId") String oAuthId);


}
