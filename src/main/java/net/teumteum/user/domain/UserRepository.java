package net.teumteum.user.domain;

import java.util.List;
import java.util.Optional;
import net.teumteum.core.security.Authenticated;
import net.teumteum.user.domain.response.UserReviewsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from users u " +
        "where u.oauth.authenticated  = :authenticated and u.oauth.oauthId = :oAuthId")
    Optional<User> findByAuthenticatedAndOAuthId(@Param("authenticated") Authenticated authenticated,
        @Param("oAuthId") String oAuthId);

    @Query("select new net.teumteum.user.domain.response.UserReviewsResponse(r,count(r)) "
        + "from users u join u.reviews r where u.id = :userId group by r")
    List<UserReviewsResponse> countUserReviewsByUserId(@Param("userId") Long userId);
}
