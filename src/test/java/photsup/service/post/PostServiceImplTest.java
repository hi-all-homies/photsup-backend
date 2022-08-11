package photsup.service.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import photsup.dao.post.PostDao;
import photsup.model.dto.PostRequest;
import photsup.model.entity.Post;
import photsup.model.entity.User;
import photsup.oauth2.UserPrincipal;
import photsup.service.jwt.TokenProvider;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {
    @Mock TokenProvider tokenProvider;
    @Mock ImageService imageService;
    @Mock PostDao postDao;

    @InjectMocks PostServiceImpl postService;
    List<Post> posts;

    @BeforeEach
    void setUp(){
        User user1 = new User();
        user1.setUserId(1l);

        User user2 = new User();
        user2.setUserId(2l);

        Post post1 = new Post();
        post1.setPostId(1L);
        post1.setAuthor(user1);
        post1.setContent("post1 content");
        post1.setCreated(new Date());
        post1.setImageUrl("post1 image url");
        post1.setAwsKey("aws key 1");
        post1.setLikes(Set.of(user2));

        Post post2 = new Post();
        post2.setPostId(2L);
        post2.setAuthor(user2);
        post2.setContent("post2 content");
        post2.setCreated(new Date());
        post2.setImageUrl("post2 image url");
        post2.setAwsKey("aws 2");
        post2.setLikes(Set.of(user1, user2));

        posts = List.of(post1, post2);

        Mockito.when(tokenProvider.verifyToken(Mockito.anyString()))
                .thenReturn(UserPrincipal.builder()
                        .id(1L)
                        .username("test_user")
                        .build());
    }


    @Test
    void savePost() throws IOException {
        PostRequest req = new PostRequest();
        req.setContent("hello world");
        req.setImage(
                new MockMultipartFile("image", "picture.png",
                        MediaType.IMAGE_PNG_VALUE, "image_content".getBytes()));

        postService.savePost("token", req);
        Mockito.verify(tokenProvider, Mockito.times(1))
                .verifyToken(Mockito.anyString());
        Mockito.verify(imageService, Mockito.times(1))
                .storeImage(req.getImage());
        Mockito.verify(postDao, Mockito.times(1))
                .savePost(Mockito.any(Post.class));


        PostRequest req2 = new PostRequest();
        req2.setContent("hello 2");
        req2.setImage(
                new MockMultipartFile("image", "text.txt",
                        MediaType.TEXT_PLAIN_VALUE, "image_content".getBytes()));

        assertThrows(RuntimeException.class, () -> postService.savePost("token", req2));

    }

    @Test
    void findPosts() {
        Mockito.when(postDao.findPosts(Mockito.any(PageRequest.class)))
                .thenReturn(posts);

        var result = postService.findPosts("token", 0);

        assertEquals(2, result.size());

        result.stream()
                .filter(postSummary -> postSummary.getPostId() == 2L)
                .forEach(postSummary -> {
                    assertEquals(2, postSummary.getLikeCount());
                    assertTrue(postSummary.isMeLiked());
                });
    }

    @Test
    void updatePost() throws IOException{
        PostRequest req = new PostRequest();
        req.setPostId(1L);
        req.setContent("hello world");
        req.setImage(
                new MockMultipartFile("image", "picture.png",
                        MediaType.IMAGE_PNG_VALUE, "image_content".getBytes()));

        var post1 = posts.get(0);
        Mockito.when(postDao.findById(1L))
                .thenReturn(Optional.of(post1));

        Mockito.when(imageService.storeImage(Mockito.any(MultipartFile.class)))
                .thenReturn(Map.of("key", "updated aws key", "url", "updated image url"));

        postService.updatePost("token", req);
        Mockito.verify(postDao, Mockito.times(1))
                .updatePost(Mockito.any(Post.class));
        Mockito.verify(imageService, Mockito.times(1))
                .deleteImage(Mockito.anyString());

        assertTrue(() -> post1.getContent().equals(req.getContent()));
        assertTrue(() -> post1.getImageUrl().equals("updated image url"));

        Mockito.when(postDao.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(posts.get(1)));
        postService.updatePost("token", req);
        Mockito.verify(postDao, Mockito.times(1))
                .updatePost(Mockito.any(Post.class));
    }


    @Test
    void deletePost(){
        Mockito.when(postDao.findById(1L))
                .thenReturn(Optional.of(posts.get(0)));

        postService.deletePost("token", 1L);
        Mockito.verify(postDao, Mockito.times(1))
                .deletePost(1L);


        Mockito.when(postDao.findById(2L))
                .thenReturn(Optional.of(posts.get(1)));

        postService.deletePost("token", 2L);
        Mockito.verify(postDao, Mockito.times(0))
                .deletePost(2L);


        Mockito.when(postDao.findById(3L))
                        .thenReturn(Optional.empty());
        assertThrows(Exception.class,() -> postService.deletePost("token", 3L));
    }
}