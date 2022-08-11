package photsup.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import photsup.dao.post.PostDao;
import photsup.model.dto.PostRequest;
import photsup.model.dto.PostSummary;
import photsup.model.entity.Post;
import photsup.model.entity.User;
import photsup.service.jwt.TokenProvider;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final TokenProvider tokenProvider;
    private final ImageService imageService;
    private final PostDao postDao;


    @Override
    public Post savePost(String token, PostRequest postRequest) {
        try {
            var image = postRequest.getImage();
            if (image == null || image.isEmpty() || !image.getContentType().contains("image")){
                throw new RuntimeException("file must be an image");
            }

            var map = this.imageService.storeImage(image);

            User currentUser = new User();
            currentUser.setUserId(retrieveCurrentUserId(token));
            Post post = new Post();
            post.setAuthor(currentUser);
            post.setCreated(new Date());
            post.setContent(postRequest.getContent());
            post.setImageUrl(map.get(ImageService.URL));

            return this.postDao.savePost(post);
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    private static final int PAGE_SIZE = 7;
    private static final Sort SORT = Sort.by(Sort.Direction.DESC, "postId");

    private Collection<PostSummary> transform(Collection<Post> posts, final long currentUser){
        return posts.stream()
                .map(p -> PostSummary.builder()
                        .postId(p.getPostId())
                        .content(p.getContent())
                        .imageUrl(p.getImageUrl())
                        .author(p.getAuthor())
                        .created(p.getCreated())
                        .likeCount(p.getLikes().size())
                        .meLiked(p.getLikes().stream()
                                .anyMatch(user -> user.getUserId().equals(currentUser)))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<PostSummary> findPosts(String token, int page) {
        var pageRequest = PageRequest.of(page, PAGE_SIZE, SORT);

        var posts = this.postDao.findPosts(pageRequest);

        return this.transform(posts, retrieveCurrentUserId(token));
    }

    @Override
    public void updatePost(String token, PostRequest postRequest) {
        var oldPost= this.postDao.findById(postRequest.getPostId())
                .orElseThrow();
        long authorId = oldPost.getAuthor().getUserId();
        long currentUserId = retrieveCurrentUserId(token);

        if (authorId == currentUserId){
            Post updatedPost = new Post();
            updatedPost.setPostId(oldPost.getPostId());
            updatedPost.setContent(postRequest.getContent());
            updatedPost.setImageUrl(oldPost.getImageUrl());

            var image = postRequest.getImage();
            if (image != null && !image.isEmpty() && image.getContentType().contains("image")){
                try {
                    var map = this.imageService.storeImage(image);
                    this.imageService.deleteImage(retrieveAwsKey(oldPost.getImageUrl()));
                    updatedPost.setImageUrl(map.get(ImageService.URL));
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            this.postDao.updatePost(updatedPost);
        }
    }

    @Override
    public boolean addLike(String token, Long postId) {
        return this.postDao.addLike(postId, retrieveCurrentUserId(token));
    }

    @Override
    public void deletePost(String token, Long postId) {
         var post = this.postDao.findById(postId)
                .orElseThrow();
         long authorId = post.getAuthor().getUserId();
         long currentUserId = retrieveCurrentUserId(token);

         if (authorId == currentUserId){
            this.postDao.deletePost(postId);
            this.imageService.deleteImage(retrieveAwsKey(post.getImageUrl()));
         }
    }

    private long retrieveCurrentUserId(String token) {
        return this.tokenProvider.verifyToken(token).getId();
    }

    private String retrieveAwsKey(String url){
        return url.replaceFirst("https://storage.yandexcloud.net/photsup/","");
    }
}
