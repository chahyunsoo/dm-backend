package com.DM.DeveloperMatching.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.Date;


@Service
@Getter
public class JwtTokenUtils {

    /**
     * JWT 토큰 발급
     * Claim은 JWT토큰에 들어갈 정보를 넣은 것
     * Claim에 userId(User 테이블에 저장되는 id), email을 저장할 것
     * 만료기간은 1일로 설정
     */
    public static String createToken(long userId, String email, String key, long expireTimeMs) {
        Date now = new Date(); // 토큰 생성 시간
        Date expiryDate = new Date(System.currentTimeMillis() + expireTimeMs); // 만료 시간 계산

        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        claims.put("email", email);

        return Jwts.builder()
                .setClaims(claims)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }


    /**
     * secretkey를 사용해서 Token Parsing
     */
    public static Claims extractClaims(String token, String secretKey) {
        // Bearer 부분 제거
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        System.out.println("body.getIssuedAt() = " + body.getIssuedAt());
        System.out.println("body.getExpiration() = " + body.getExpiration());
        return body;
    }

    /**
     * Claim에서 userId를 추출
     */
    public static Long extractUserId(String token, String secretKey) {
        return Long.valueOf(extractClaims(token, secretKey)
                .get("userId").toString());
    }

    /**
     * Claim에서 email을 추출
     */
    public static String extractUserEmail(String token, String secretKey) {
        return extractClaims(token, secretKey)
                .get("email").toString();
    }

    /**
     * 발급된 Token이 만료 시간이 지났는지 check
     */
    public static boolean isExpired(String jwtToken, String secretKey) {
        try {
            Claims claims = extractClaims(jwtToken, secretKey);
//            System.out.println("claims = " + claims);

            Date expiration = claims.getExpiration();
//            System.out.println("expiration = " + expiration);

            Date now = new Date();
//            System.out.println("Token expiration time: " + expiration);
//            System.out.println("Current time: " + now);

            return expiration.before(now);
        } catch (Exception e) {
            System.out.println("Error checking if token is expired: " + e.getMessage());
            return true;
        }
    }
}
