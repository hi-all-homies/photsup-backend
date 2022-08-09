package photsup.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import photsup.service.jwt.TokenProvider;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    @Value("${jwt.auth.header:X-Auth-Token}")
    private String headerName;

    @Value("${jwt.query.param:jwt}")
    private String paramName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = request.getHeader(this.headerName);
            if (token == null)
                token = request.getParameter(this.paramName);

            if (StringUtils.hasText(token)) {
                var userPrincipal = this.tokenProvider.verifyToken(token);

                var securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(
                        new OAuth2AuthenticationToken(
                                userPrincipal, userPrincipal.getAuthorities(), userPrincipal.getName()));
                SecurityContextHolder.setContext(securityContext);
            }
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
