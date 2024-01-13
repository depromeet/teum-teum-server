package net.teumteum.user.controller;

import io.sentry.Sentry;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.core.security.service.SecurityService;
import net.teumteum.user.domain.request.UserUpdateRequest;
import net.teumteum.user.domain.response.FriendsResponse;
import net.teumteum.user.domain.response.InterestQuestionResponse;
import net.teumteum.user.domain.response.UserGetResponse;
import net.teumteum.user.domain.response.UsersGetByIdResponse;
import net.teumteum.user.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final ApplicationContext applicationContext;
    private final UserService userService;
    private final SecurityService securityService;

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserGetResponse getUserById(@PathVariable("userId") Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public UsersGetByIdResponse getUsersById(@RequestParam("id") String userIds) {
        var parsedUserIds = Arrays.stream(userIds.split(","))
            .map(Long::valueOf)
            .toList();

        return userService.getUsersById(parsedUserIds);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateUser(@RequestBody UserUpdateRequest request) {
        userService.updateUser(getCurrentUserId(), request);
    }

    @PostMapping("/{friendId}/friends")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable("friendId") Long friendId) {
        userService.addFriends(getCurrentUserId(), friendId);
    }

    @GetMapping("/{userId}/friends")
    @ResponseStatus(HttpStatus.OK)
    public FriendsResponse findFriends(@PathVariable("userId") Long userId) {
        return userService.findFriendsByUserId(userId);
    }

    @GetMapping("/interests")
    @ResponseStatus(HttpStatus.OK)
    public InterestQuestionResponse getInterestQuestion(@RequestParam("user-id") List<Long> userIds,
        @RequestParam("type") String balance) {
        return userService.getInterestQuestionByUserIds(userIds, balance);
    }

    @DeleteMapping("/withdraws")
    @ResponseStatus(HttpStatus.OK)
    public void withdraw() {
        userService.withdraw(getCurrentUserId());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        Sentry.captureException(illegalArgumentException);
        return ErrorResponse.of(illegalArgumentException);
    }

    private Long getCurrentUserId() {
        return securityService.getCurrentUserId();
    }
}
