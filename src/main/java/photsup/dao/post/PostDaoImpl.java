package photsup.dao.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import photsup.dao.repository.PostRepository;
import photsup.model.entity.Post;
import photsup.model.entity.User;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostDaoImpl implements PostDao {
    private final PostRepository postRepo;

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
    public Optional<Post> findById(Long postId) {
        return this.postRepo.findPostById(postId);
    }

    @Override
    public long countUserPosts(User user){
        var postProbe = new Post();
        var userProbe = new User();
        userProbe.setUserId(user.getUserId());
        postProbe.setAuthor(userProbe);
        return postRepo.count(Example.of(postProbe));
    }

    @Override
    public long countLikes(User user) {
        return this.postRepo.countLikes(user.getUserId());
    }
}
