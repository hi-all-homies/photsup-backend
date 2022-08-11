package photsup.dao.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import photsup.dao.repository.PostRepository;
import photsup.dao.repository.UserRepository;
import photsup.model.entity.Post;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostDaoImpl implements PostDao {
    private final PostRepository postRepo;
    private final UserRepository userRepo;

    @Override
    public Post savePost(Post post) {
        return this.postRepo.save(post);
    }

    @Override
    public Collection<Post> findPosts(Pageable pageable) {
        var ids = this.postRepo.findPostIds(pageable);
        return this.postRepo.findPostsByIds(ids);
    }

    @Override
    public void deletePost(Long postId) {
        this.postRepo.deleteById(postId);
    }

    @Override
    @Transactional
    public void updatePost(Post post) {
        this.postRepo.updatePost(
                post.getPostId(), post.getContent(), post.getImageUrl());
    }

    @Override
    @Transactional
    public boolean addLike(Long postId, Long userId) {
        var user = this.userRepo.findById(userId)
                .orElseThrow();

        var post = this.postRepo.findPostById(postId)
                .orElseThrow();
        boolean result;

        if (post.getLikes().contains(user)){
            result = !post.getLikes().remove(user);
        }
        else{
            result = post.getLikes().add(user);
        }
        return result;
    }

    @Override
    public Optional<Post> findById(Long postId) {
        return this.postRepo.findPostById(postId);
    }
}
