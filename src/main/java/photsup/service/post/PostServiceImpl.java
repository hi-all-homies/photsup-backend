package photsup.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import photsup.dao.post.PostDao;
import photsup.dao.user.UserDao;
import photsup.model.dto.CommentRequest;
import photsup.model.dto.PostGetLiked;
import photsup.model.dto.PostRequest;
import photsup.model.dto.PostSummary;
import photsup.model.entity.Comment;
import photsup.model.entity.Post;
import photsup.model.entity.User;
import photsup.service.jwt.TokenProvider;
import photsup.service.notification.NotificationService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final TokenProvider tokenProvider;
    private final ImageService imageService;
    private final PostDao postDao;
    private final UserDao userDao;
    private final NotificationService notificationService;


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
            post.setCreated(LocalDateTime.now());
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

    private Stream<PostSummary> transform(Collection<Post> posts, final long currentUser){
        return posts.stream()
                .sorted((p1,p2) -> p2.getPostId().compareTo(p1.getPostId()))
                .map(p -> PostSummary.builder()
                        .postId(p.getPostId())
                        .content(p.getContent())
                        .imageUrl(p.getImageUrl())
                        .author(p.getAuthor())
                        .created(p.getCreated())
                        .likeCount(p.getLikes().size())
                        .commentCount(p.getComments().size())
                        .meLiked(p.getLikes().stream()
                                .anyMatch(user -> user.getUserId().equals(currentUser)))
                        .comments(p.getComments())
                        .build());
    }

    @Override
    public Collection<PostSummary> findPosts(String token, int page) {
        var pageRequest = PageRequest.of(page, PAGE_SIZE, SORT);

        var posts = this.postDao.findPosts(pageRequest);

        return this.transform(posts, retrieveCurrentUserId(token))
                .collect(Collectors.toList());
    }


    @Override
    public PostSummary findById(String token, long postId) {
        var post = this.postDao.findById(postId)
                .orElseThrow();

        return transform(List.of(post), retrieveCurrentUserId(token))
                .findFirst().get();
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
    @Transactional
    public boolean addLike(String token, Long postId) {
        long userId =  retrieveCurrentUserId(token);
        var user = this.userDao.findById(userId).orElseThrow();
        var post = this.postDao.findById(postId).orElseThrow();

        boolean result;

        if (post.getLikes().contains(user)){
            result = !post.getLikes().remove(user);
        }
        else{
            result = post.getLikes().add(user);

            var notification = PostGetLiked.builder()
                    .liker(user.getUsername())
                    .postId(postId)
                    .receiver(post.getAuthor().getUniqueKey()).build();

            notificationService.sendNotification(notification);
        }
        return result;
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


    @Transactional
    @Override
    public Comment addComment(String token, CommentRequest commentRequest) {
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setAuthor(retrieveCurrentUser(token));

        var post = this.postDao.findById(commentRequest.getPostId())
                .orElseThrow();
        post.getComments().add(comment);

        return comment;
    }

    private long retrieveCurrentUserId(String token) {
        return this.tokenProvider.verifyToken(token).getId();
    }

    private User retrieveCurrentUser(String token) {
        var usrPrincipal = this.tokenProvider.verifyToken(token);
        User user = new User();
        user.setUserId(usrPrincipal.getId());
        user.setUsername(usrPrincipal.getUsername());
        user.setAvatarUrl(usrPrincipal.getAvatarUrl());
        user.setUniqueKey(usrPrincipal.getUniqueKey());
        return user;
    }

    private String retrieveAwsKey(String url){
        return url.replaceFirst("https://storage.yandexcloud.net/photsup/","");
    }
}
