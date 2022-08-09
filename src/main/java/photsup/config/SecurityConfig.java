package photsup.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import photsup.oauth2.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2RequestRepository oAuth2RequestRepository;
    private final SuccessHandler successHandler;
    private final TokenFilter tokenFilter;
    private final OidcCustomUserService oidcUserService;
    private final OAuth2CustomUserService oauthUserService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.cors().and()
                .formLogin().disable()
                .logout().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement(customizer ->
                        customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(customizer ->
                        customizer.anyRequest().authenticated())

                .oauth2Login()
                .authorizationEndpoint(customizer ->
                        customizer.authorizationRequestRepository(oAuth2RequestRepository))

                .successHandler(successHandler)
                .failureHandler(failureHandler)

                .userInfoEndpoint(customizer ->
                        customizer.oidcUserService(oidcUserService)
                                .userService(oauthUserService))
                .and()
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(customizer ->
                        customizer.authenticationEntryPoint(authEntryPoint))
                .build();
    }

    private final AuthenticationFailureHandler failureHandler = (request, response, exception) ->
            response.sendError(403, exception.getMessage());

    private final AuthenticationEntryPoint authEntryPoint = (request, response, authException) ->
                response.sendError(403, authException.getMessage());
}
