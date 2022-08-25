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

        Post post3 = new Post();
        post3.setAuthor(user1);
        post3.setContent("post2 content");
        post3.setCreated(LocalDateTime.now());
        post3.setImageUrl("post2 image url");
        this.postRepo.save(post3);
    }

    @AfterAll
    void tearDown(){
        this.postRepo.deleteAll();
        this.userRepo.deleteAll();
    }

    @Test
    void countUserPosts() {
        User user3 = new User();
        user3.setUserId(45L);
        User user1 = new User();
        user1.setUserId(1L);

        long result1 = postDao.countUserPosts(user3);
        assertEquals(0, result1);

        long result2 = postDao.countUserPosts(user1);
        assertEquals(1, result2);
    }

}