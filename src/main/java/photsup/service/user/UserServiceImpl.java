package photsup.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import photsup.dao.post.PostDao;
import photsup.dao.user.UserDao;
import photsup.model.dto.UpdateStatusRequest;
import photsup.model.dto.UserSummary;
import photsup.model.entity.User;
import photsup.oauth2.UserPrincipal;
import photsup.service.jwt.TokenProvider;
import java.util.Collection;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final PostDao postDao;
    private final TokenProvider tokenProvider;

    @Override
    public UserSummary findUser(String uniqueKey) {
        var user = this.userDao.findByUniqueKey(uniqueKey)
                .orElseThrow();
        long postCount = this.postDao.countUserPosts(user);
        long likeCount = this.postDao.countLikes(user);

        return UserSummary.builder().userId(user.getUserId())
                .username(user.getUsername()).uniqueKey(user.getUniqueKey())
                .status(user.getStatus()).avatarUrl(user.getAvatarUrl())
                .postsSent(postCount).likesReceived(likeCount)
                .build();
    }

    @Override
    public OAuth2User storeUser(OAuth2User oAuth2User, String provider) {
        User user = userFactory(oAuth2User, provider);

        var optUser = this.userDao.findByUniqueKey(user.getUniqueKey());
        if (optUser.isPresent()){
            this.userDao.updateUser(optUser.get());
            user = optUser.get();
        }
        else
            user = this.userDao.saveUser(user);

        return UserPrincipal.builder()
                .id(user.getUserId())
                .username(user.getUsername())
                .uniqueKey(user.getUniqueKey())
                .avatarUrl(user.getAvatarUrl())
                .attributes(oAuth2User.getAttributes())
                .authorities((Collection<GrantedAuthority>) oAuth2User.getAuthorities())
                .build();
    }

    @Override
    @Transactional
    public void updateUserStatus(String token, UpdateStatusRequest request){
        String uniqueKey = this.tokenProvider.verifyToken(token).getUniqueKey();

        var currentUser = this.userDao.findByUniqueKey(uniqueKey)
                .orElseThrow();

        currentUser.setStatus(request.getStatus());
    }
}
