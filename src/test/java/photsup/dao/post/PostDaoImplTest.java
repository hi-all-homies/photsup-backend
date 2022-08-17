package photsup.dao.post;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import photsup.dao.repository.PostRepository;
import photsup.dao.repository.UserRepository;
import photsup.model.entity.Post;
import photsup.model.entity.User;

import java.time.LocalDateTime;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostDaoImplTest {
    @Autowired
    private PostDaoImpl postDao;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PostRepository postRepo;


    @BeforeAll
    void setUp(){
        User user1 = new User();
        user1.setUsername("google_user");
        user1.setUniqueKey("@google.com");
        user1.setAvatarUrl("https://ngfnui455941");
        user1 = this.userRepo.save(user1);

        User user2 = new User();
        user2.setUsername("github_user");
        user2.setUniqueKey("git_uniqueName");
        user2.setAvatarUrl("https://ngfnui455941");
        user2 = this.userRepo.save(user2);

        Post post1 = new Post();
        post1.setAuthor(user1);
        post1.setContent("post1 content");
        post1.setCreated(LocalDateTime.now());
        post1.setImageUrl("post1 image url");
        this.postRepo.save(post1);

        Post post2 = new Post();
        post2.setAuthor(user2);
        post2.setContent("post2 content");
        post2.setCreated(LocalDateTime.now());
        post2.setImageUrl("post2 image url");
        this.postRepo.save(post2);
    }

    @AfterAll
    void tearDown(){
        this.postRepo.deleteAll();
        this.userRepo.deleteAll();
    }


    @Test
    void addLike() {
        var result1 = this.postDao.addLike(1L, 2L);
        assertTrue(result1);
        var result2 = this.postDao.addLike(1L, 2L);
        assertFalse(result2);

        assertThrows(NoSuchElementException.class,
                () -> this.postDao.addLike(5L, 2L));

        assertThrows(NoSuchElementException.class,
                () -> this.postDao.addLike(1L, 4L));
    }
}