package photsup.service.user;

import org.springframework.security.oauth2.core.user.OAuth2User;
import photsup.model.dto.UserSummary;
import photsup.model.entity.GitHubUser;
import photsup.model.entity.GoogleUser;
import photsup.model.entity.User;

public interface UserService {

    UserSummary findUser(String uniqueKey);

    OAuth2User storeUser(OAuth2User oAuth2User, String provider);

    String retrieveUniqueKey(String token);

    default User userFactory(OAuth2User oAuth2User, String provider){
        User user;
        if ("google".equals(provider))
            user = new GoogleUser(oAuth2User);
        else if ("github".equals(provider))
            user = new GitHubUser(oAuth2User);
        else
            throw new UnsupportedOperationException();
        return user;
    }
}
