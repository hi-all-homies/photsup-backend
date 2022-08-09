package photsup.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import photsup.service.user.UserService;

@Component
@RequiredArgsConstructor
public class OidcCustomUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {
    private final UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        var delegate = new OidcUserService();

        String provider = userRequest.getClientRegistration().getRegistrationId();

        var loadedUser = delegate.loadUser(userRequest);

        return (OidcUser) this.userService.storeUser(loadedUser, provider);
    }
}
