package photsup.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;
import photsup.model.entity.User;
import javax.persistence.Entity;

@Entity
public class GoogleUser extends User {

    public GoogleUser(OAuth2User oAuth2User){
        setUsername(oAuth2User.getAttribute("name"));
        setAvatarUrl(oAuth2User.getAttribute("picture"));
        setUniqueKey(oAuth2User.getAttribute("email"));
    }
}
