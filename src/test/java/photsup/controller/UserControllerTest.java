package photsup.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import photsup.oauth2.UserPrincipal;
import photsup.service.jwt.TokenProvider;
import photsup.service.user.UserService;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@Import(EndpointsTestConfig.class)
class UserControllerTest {
    @Autowired MockMvc mockMvc;
    @MockBean UserService userService;
    @Autowired TokenProvider tokenProvider;
    String token;

    @BeforeEach
    public void setUp(){
        token = tokenProvider.generateToken(UserPrincipal.builder()
                        .id(1L)
                        .username("Bob")
                        .uniqueKey("@mail.com")
                        .avatarUrl("pic.png")
                .build());
    }

    @Test
    void getUser() throws Exception {
        mockMvc.perform(get("/users/@gmail.com"))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1))
                .findUser("@gmail.com");
    }
}