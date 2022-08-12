package photsup.controller;


import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import static org.hamcrest.CoreMatchers.is;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import photsup.model.dto.PostRequest;
import photsup.model.dto.PostSummary;
import photsup.model.entity.Post;
import photsup.model.entity.User;
import photsup.oauth2.UserPrincipal;
import photsup.service.jwt.TokenProvider;
import photsup.service.post.PostService;
import java.util.Date;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PostController.class)
@Import({EndpointsTestConfig.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired
    TokenProvider tokenProvider;
    @MockBean PostService postService;
    User user;
    Post post;
    String token;

    @BeforeAll
    void setUp(){
        user = new User();
        user.setUserId(1L);
        user.setAvatarUrl("https://qwert/zxc");
        user.setUsername("user");
        user.setUniqueKey("@google.com");
        post = new Post();
        post.setAuthor(user);
        post.setPostId(1L);
        post.setCreated(new Date());
        post.setImageUrl("image url");
        post.setContent("hello world");

        token = tokenProvider.generateToken(UserPrincipal.builder()
                        .id(user.getUserId())
                        .username(user.getUsername())
                        .avatarUrl(user.getAvatarUrl())
                        .uniqueKey(user.getUniqueKey())
                .build());
    }

    @Test
    void findPosts() throws Exception {
        var posts = new Random().longs(3).boxed()
                .map(r -> PostSummary.builder()
                        .postId(r)
                        .meLiked(false)
                        .content("new post "+r)
                        .likeCount(0)
                        .created(new Date())
                        .author(user)
                        .imageUrl("https://image"+r)
                        .build())
                .toList();

        Mockito.when(postService.findPosts(Mockito.anyString(), Mockito.anyInt()))
                .thenReturn(posts);

        mockMvc.perform(get("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Auth-Token", token)
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].meLiked", is(false)));


        mockMvc.perform(get("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Auth-Token", token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void storePost() throws Exception {
       Mockito.when(postService.savePost(Mockito.anyString(), Mockito.any(PostRequest.class)))
               .thenReturn(post);

       var image = new MockMultipartFile(
               "image", "image.png", "image/png", "image bytes".getBytes());

       mockMvc.perform(multipart("/posts")
                       .file(image)
                       .param("content", "first post")
                       .header("X-Auth-Token", token)
               )
               .andExpect(status().isOk());
    }

    @Test
    void addLike() throws Exception {
        mockMvc.perform(post("/posts/1/like")
                        .header("X-Auth-Token", token))
                .andExpect(status().isOk());

        Mockito.verify(postService, Mockito.times(1))
                .addLike(Mockito.anyString(), Mockito.anyLong());
    }

    @Test
    void updatePost() throws Exception {
        var image = new MockMultipartFile(
                "image", "upd.png", "image/png", "upd image".getBytes());

        mockMvc.perform(multipart(HttpMethod.PUT, "/posts/1")
                        .file(image)
                        .param("content", "hello world")
                        .header("X-Auth-Token", token))
                .andExpect(status().isOk());

        Mockito.verify(postService, Mockito.times(1))
                .updatePost(Mockito.anyString(), Mockito.any(PostRequest.class));
    }

    @Test
    void deletePost() throws Exception {
        mockMvc.perform(delete("/posts/1")
                        .header("X-Auth-Token", token))
                .andExpect(status().isOk());

        Mockito.verify(postService, Mockito.times(1))
                .deletePost(Mockito.anyString(), Mockito.anyLong());
    }
}