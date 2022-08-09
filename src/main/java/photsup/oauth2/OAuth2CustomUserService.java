package photsup.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import photsup.service.user.UserService;

@Component
@RequiredArgsConstructor
public class OAuth2CustomUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var delegate = new DefaultOAuth2UserService();

        String provider = userRequest.getClientRegistration().getRegistrationId();

        var loadedUser = delegate.loadUser(userRequest);

        return this.userService.storeUser(loadedUser, provider);
    }
}
