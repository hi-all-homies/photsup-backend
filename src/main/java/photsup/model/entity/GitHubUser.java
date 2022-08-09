package photsup.model.entity;

import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import photsup.model.entity.User;
import javax.persistence.Entity;

@Entity
@NoArgsConstructor
public class GitHubUser extends User {

    public GitHubUser(OAuth2User oAuth2User){
        setUsername(oAuth2User.getAttribute("login"));
        setAvatarUrl(oAuth2User.getAttribute("avatar_url"));
        setUniqueKey("github_".concat(oAuth2User.getAttribute("login")));
    }
}
