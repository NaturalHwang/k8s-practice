package beyond.orderSystem.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private int experiation;

    @Value("${jwt.secretKeyRt}")
    private String secretKeyRt;

    @Value("${jwt.expirationRt}")
    private int experiationRt;

    public String createToken(String email, String role){
//        claims는 사용자 정보이자 paylord 정보
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now) // 생성 시간
                .setExpiration(new Date(now.getTime() + experiation*60*1000L))  // 만료 시간(30분 분 * 초 * 밀리초)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        return token;
    }

    public String createRefreshToken(String email, String role){
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + experiationRt*60*1000L))
                .signWith(SignatureAlgorithm.HS256, secretKeyRt)
                .compact();
        return token;
    }
}