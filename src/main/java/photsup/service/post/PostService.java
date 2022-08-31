package photsup.service.post;

import photsup.model.dto.CommentRequest;
import photsup.model.dto.PostRequest;
import photsup.model.dto.PostSummary;
import photsup.model.entity.Comment;
import photsup.model.entity.Post;
import java.util.Collection;

public interface PostService {

    Post savePost(String token, PostRequest postRequest);

    Collection<PostSummary> findPosts(String token, int page);

    void updatePost(String token, PostRequest postRequest);

    boolean addLike(String token, Long postId);

    void deletePost(String token, Long postId);

    Comment addComment(String token, CommentRequest commentRequest);
}
