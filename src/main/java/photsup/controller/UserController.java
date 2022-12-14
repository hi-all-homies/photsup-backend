package photsup.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import photsup.model.dto.UpdateStatusRequest;
import photsup.model.dto.UserSummary;
import photsup.service.user.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{key}")
    public UserSummary getUser(@PathVariable("key") String key){
        return this.userService.findUser(key);
    }

    @PutMapping
    public ResponseEntity<?> updateStatus(@RequestHeader("X-Auth-Token") String token,
                                          @RequestBody UpdateStatusRequest updRequest){

        this.userService.updateUserStatus(token,updRequest);
        return ResponseEntity.ok().build();
    }
}
