package photsup.service.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import photsup.oauth2.UserPrincipal;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Service
public class TokenProviderHmacSha implements TokenProvider {
    @Value("${JWT_SECRET_KEY}")
    private String secret;

    @Value("${jwt.expiration.time}")
    private Long expirationTime;

    @Value("${jwt.prefix}")
    private String prefix;

    private SecretKey secretKey;

    @PostConstruct
    public void init(){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public String generateToken(UserPrincipal userPrincipal) {
        String token = Jwts.builder()
                .setClaims(
                        Map.of(
                                "sub", userPrincipal.getUniqueKey(),
                                "id", userPrincipal.getId(),
                                "username", userPrincipal.getUsername(),
                                "avatarUrl", userPrincipal.getAvatarUrl()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + this.expirationTime))
                .signWith(this.secretKey)
                .compact();

        StringBuilder sBuilder = new StringBuilder(this.prefix);
        sBuilder.append(token);
        return sBuilder.toString();
    }

    @Override
    public UserPrincipal verifyToken(String token) {
        Claims claims = getClaims(token);

        return UserPrincipal.builder()
                .id(claims.get("id", Long.class))
                .uniqueKey(claims.getSubject())
                .avatarUrl(claims.get("avatarUrl", String.class))
                .username(claims.get("username", String.class))
                .build();
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.secretKey).build()
                .parseClaimsJws(token.replaceFirst(this.prefix, ""))
                .getBody();
    }
}
