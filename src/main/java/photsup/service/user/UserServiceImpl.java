package photsup.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import photsup.dao.user.UserDao;
import photsup.model.entity.User;
import photsup.oauth2.UserPrincipal;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public User findUser(String uniqueKey) {
        return this.userDao.findByUniqueKey(uniqueKey)
                .orElseThrow();
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
}
