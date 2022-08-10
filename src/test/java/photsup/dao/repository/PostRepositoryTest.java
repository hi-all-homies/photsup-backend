package photsup.dao.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import photsup.model.entity.Post;
import photsup.model.entity.User;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepo;
    @Autowired
    private UserRepository userRepo;

    @BeforeAll
    void setUp() {
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
        post1.setCreated(new Date());
        post1.setImageUrl("post1 image url");
        post1.setAwsKey("awsKey1");
        this.postRepo.save(post1);

        Post post2 = new Post();
        post2.setAuthor(user2);
        post2.setContent("post2 content");
        post2.setCreated(new Date());
        post2.setImageUrl("post2 image url");
        post2.setAwsKey("awsKey2");
        this.postRepo.save(post2);
    }

    @AfterAll
    void tearDown() {
        this.postRepo.deleteAll();
        this.userRepo.deleteAll();
    }

    @Test
    void findPostIds() {
        var ids = this.postRepo.findPostIds(PageRequest.of(0,5));
        assertEquals(2, ids.size());
    }

    @Test
    void findPostsByIds() {
        var posts = this.postRepo.findPostsByIds(List.of(1L,2L));
        assertEquals(2, posts.size());
        boolean result = posts.stream()
                .anyMatch(p -> p.getContent().equals("post1 content"));
        assertTrue(result);
    }

    @Test
    void findPostById() {
        var post = this.postRepo.findPostById(2L);
        assertNotNull(post.get());
        assertEquals("post2 content", post.get().getContent());

        final var post2 = this.postRepo.findPostById(5L);
        assertThrows(NoSuchElementException.class, () -> post2.get());
    }

    @Test
    @Transactional
    void updatePost(){
        int res = this.postRepo.updatePost(1L,
                "post1 content",
                "updated post1 imageUrl", "awsKey upd");
        assertEquals(1, res);

        var post = this.postRepo.findById(1L).orElseThrow();
        assertEquals("updated post1 imageUrl", post.getImageUrl());

        int res2 = this.postRepo.updatePost(5L,
                "post5 content",
                "updated post5 imageUrl", "awsKey5");

        assertEquals(0, res2);
    }
}