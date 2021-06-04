package com.du.du_blog.utils;

import io.jsonwebtoken.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@Data
@ConfigurationProperties(prefix = "blog.jwt")
public class JWTUtils {
    private long expire;
    private String secure;
    private String header;

    //生成jwt
    public String createToken(String username){
        Date nowTime = new Date();
        Date expireTime = new Date(nowTime.getTime() + 1000 * expire);
        return Jwts.builder()
                //token类型
                .setHeaderParam("typ","JWT")
                .setSubject(username)
                .setIssuedAt(nowTime)
                .setExpiration(expireTime)
                .signWith(SignatureAlgorithm.HS512,secure)
                .compact();
    }

    //解析jwt
    public Claims getClaimsByToken(String jwt){
        try {
            return Jwts.parser()
                    .setSigningKey(secure)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否token过期
     * @param claims
     * @return
     */
    public boolean isTokenExpired(Claims claims){
        return claims.getExpiration().before(new Date());
    }
}
