package photsup.oauth2;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import java.util.Collection;
import java.util.Map;

@Builder
@Getter
public class UserPrincipal implements OidcUser {
    private final Long id;
    private final String username;
    private final String uniqueKey;
    private final String avatarUrl;
    private final Collection<GrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    @Override
    public Map<String, Object> getClaims() {
        return this.attributes;
    }

    @Override
    public String getName() {
        return this.username;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }

}
