package photsup.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import photsup.model.entity.User;
import photsup.service.user.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public User getCurrentUser(@RequestHeader("X-Auth-Token") String token){
        String uniqueKey = this.userService.retrieveUniqueKey(token);
        return this.userService.findUser(uniqueKey);
    }

    @GetMapping("/{key}")
    public User getUser(@PathVariable("key") String key){
        return this.userService.findUser(key);
    }
}
