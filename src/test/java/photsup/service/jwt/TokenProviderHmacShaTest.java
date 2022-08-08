package photsup.service.jwt;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import photsup.oauth2.UserPrincipal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@Import(TokenProviderHmacSha.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource("classpath:application.properties")
class TokenProviderHmacShaTest {
    @Autowired
    private TokenProviderHmacSha tokenProvider;
    private UserPrincipal userPrincipal;

    @BeforeAll
    void init(){
        this.userPrincipal = UserPrincipal.builder()
                .id(1L)
                .avatarUrl("user avatar url")
                .username("user1")
                .uniqueKey("unique_key")
                .build();
    }

    @Test
    void generateToken() {
        String token =this.tokenProvider.generateToken(this.userPrincipal);
        assertNotNull(token);
        System.out.println(token);
    }

    @Test
    void verifyToken() {
        String token = this.tokenProvider.generateToken(this.userPrincipal);

        var user = this.tokenProvider.verifyToken(token);

        assertEquals(this.userPrincipal.getUniqueKey(), user.getUniqueKey());
        assertEquals(this.userPrincipal.getUsername(), user.getUsername());
        assertEquals(this.userPrincipal.getAvatarUrl(), user.getAvatarUrl());
    }
}