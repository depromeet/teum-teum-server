package net.teumteum.user.controller;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import net.teumteum.core.context.LoginContext;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.user.domain.request.UserUpdateRequest;
import net.teumteum.user.domain.response.UserGetResponse;
import net.teumteum.user.domain.response.UsersGetByIdResponse;
import net.teumteum.user.service.UserService;
import org.springframework.http.HttpStatus;
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

    private final UserService userService;
    private final LoginContext loginContext;

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
        userService.updateUser(request);
    }

    @PostMapping("/{friendId}/friends")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable("friendId") Long friendId) {
        userService.addFriends(loginContext.getUserId(), friendId);
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        return ErrorResponse.of(illegalArgumentException);
    }
}
