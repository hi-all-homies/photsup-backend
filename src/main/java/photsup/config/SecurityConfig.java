package photsup.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import photsup.oauth2.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2RequestRepository oAuth2RequestRepository;
    private final SuccessHandler successHandler;
    private final TokenFilter tokenFilter;
    private final FailureHandler failureHandler;
    private final OidcCustomUserService oidcUserService;
    private final OAuth2CustomUserService oauthUserService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.cors().and()
                .formLogin().disable()
                .logout().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement(smc ->
                        smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.anyRequest().permitAll())
                .oauth2Login()
                .authorizationEndpoint(c ->
                        c.authorizationRequestRepository(oAuth2RequestRepository))
                .successHandler(successHandler)
                .failureHandler(failureHandler)
                .userInfoEndpoint(c ->
                        c.oidcUserService(oidcUserService).userService(oauthUserService))
                .and()
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
