package com.example.ECommerce.Platform.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    String secretKey = "YS1zdHJpbmctc2VjcmV0LWF0LWxlYXN0LTI1Ni1iaXRzLWxvbmc=";

    public String generateToken(String email,String role){
        return Jwts.builder()
                .subject(email)
                .claim("roles", List.of(role))
                .signWith((SecretKey) key())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000*60*60))
                .compact();
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public String getJwtFromHeader(HttpServletRequest request){
        String bearer = request.getHeader("Authorization");
        if(bearer!=null && bearer.startsWith("Bearer ")) return bearer.substring(7);
        return null;
    }

    public String getEmailFromJwt(String jwt){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(jwt)
                .getPayload().getSubject();
    }

    public Boolean validateToken(String jwt){
        try{
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(jwt);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
