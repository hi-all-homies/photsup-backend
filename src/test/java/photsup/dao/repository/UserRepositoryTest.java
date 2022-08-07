package photsup.dao.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import photsup.model.entity.User;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepo;
    private User user;

    @BeforeEach
    void init(){
        User user = new User();
        user.setUsername("test_user");
        user.setUniqueKey("unique_key");
        user.setAvatarUrl("https://ngfnui455941");
        this.user = this.userRepo.save(user);
    }

    @AfterEach void cleanUp(){
        this.userRepo.deleteAll();
    }

    @Test
    void findByUniqueKey() {
        var result = this.userRepo.findByUniqueKey("unique_key");
        assertDoesNotThrow(result::get);
        assertEquals("test_user",result.get().getUsername());
    }

    @Test
    @Transactional
    void updateUser() {
        int result = this.userRepo.updateUser(this.user.getUserId(), "https://updated");
        assertEquals(1, result);
    }
}