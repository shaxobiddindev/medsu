package uz.medsu.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${my_token.key}")
    String key;
    @Value("${my_token.expire_time}")
    Long expireTime;
    @Value("${my_token.expire_pass_time}")
    Long expirePassTime;

    public String generateToken(UserDetails userDetails) {
        Date date = new Date(System.currentTimeMillis()+expireTime);
        return Jwts
                .builder()
                .setIssuedAt(new Date())
                .setSubject(userDetails.getUsername())
                .setExpiration(date)
                .signWith(signKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateToken(String subject) {
        return Jwts
                .builder()
                .setIssuedAt(new Date())
                .setSubject(subject)
                .signWith(signKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    public Key signKey() {
       return Keys.hmacShaKeyFor(key.getBytes());
    }

    public String getSubject(String auth) {
        String subject ="";
        try {
            subject = Jwts
                    .parser()
                    .setSigningKey(signKey())
                    .build()
                    .parseClaimsJws(auth)
                    .getBody()
                    .getSubject()
                    .toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return subject;
    }
}



