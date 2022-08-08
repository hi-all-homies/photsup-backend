package photsup.dao.post;

import org.springframework.data.domain.Pageable;
import photsup.model.entity.Post;
import java.util.Collection;

public interface PostDao {
    Post savePost(Post post);

    Collection<Post> findPosts(Pageable pageable);

    void deletePost(Long postId);

    void updatePost(Post post);

    boolean addLike(Long postId, Long userId);
}
