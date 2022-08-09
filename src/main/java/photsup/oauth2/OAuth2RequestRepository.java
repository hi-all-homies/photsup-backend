package photsup.oauth2;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class OAuth2RequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    private final Map<String, OAuth2AuthorizationRequest> requests =
            new ConcurrentHashMap<>(50);

    private final static String STATE = "state";

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return this.requests.get(request.getParameter(STATE));
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        this.requests.put(authorizationRequest.getState(), authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return this.requests.remove(request.getParameter(STATE));
    }
}