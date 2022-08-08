package photsup.service.jwt;

import photsup.oauth2.UserPrincipal;

public interface TokenProvider {

    String generateToken(UserPrincipal userPrincipal);

    UserPrincipal verifyToken(String token);
}
