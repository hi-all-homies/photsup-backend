package photsup.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import photsup.model.dto.CommentRequest;
import photsup.model.dto.PostRequest;
import photsup.model.dto.PostSummary;
import photsup.model.entity.Comment;
import photsup.model.entity.Post;
import photsup.service.post.PostService;
import java.util.Collection;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public Collection<PostSummary> findPosts(
            @RequestHeader("X-Auth-Token") String token,
            @RequestParam int page){

        return this.postService.findPosts(token, page);
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Post storePost(
            @RequestHeader("X-Auth-Token") String token,
            @ModelAttribute PostRequest postRequest){

        return this.postService.savePost(token, postRequest);
    }

    @PostMapping("/{id}/like")
    public boolean addLike(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable("id") long postId){

        return this.postService.addLike(token, postId);
    }


    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePost(
            @RequestHeader("X-Auth-Token") String token,
            @ModelAttribute PostRequest postRequest,
            @PathVariable("id") long postId){

        postRequest.setPostId(postId);
        this.postService.updatePost(token, postRequest);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable("id") long postId){

        this.postService.deletePost(token, postId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Comment> addComment(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable("id") long postId,
            @RequestBody CommentRequest commentRequest){

        commentRequest.setPostId(postId);
        return ResponseEntity.ok(
                this.postService.addComment(token, commentRequest));
    }
}
