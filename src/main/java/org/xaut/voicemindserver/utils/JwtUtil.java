package org.xaut.voicemindserver.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long expirationMillis;

    //初始化工具类
    public JwtUtil(@Value("${voiceMind.jwt.secret-key}") String secretKey,
                   @Value("${voiceMind.jwt.expiration-millis}")long expirationMillis) {
        //JWT签名和验证的密钥，由Keys.hmacShaKeyFor把字符串转成符合算法要求的Key对象。
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        //获取过期时间
        this.expirationMillis = expirationMillis;
    }

    /**
     * 从请求头 Authorization 里解析 token 并返回用户ID（subject）
     * 返回 null 表示无效或者无token
     */
    public String parseUserIdFromAuthHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        if (!validateToken(token)) {
            return null;
        }
        return getSubjectFromToken(token);
    }

    // 生成token，放用户id或用户名做subject
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)//把用户名或者用户ID放进token的“sub”字段里
                .setIssuedAt(new Date())//设置日期
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))//设置过期时间
                .signWith(key, SignatureAlgorithm.HS256)//使用签名算法签名
                .compact();
    }

    /**
     * 解析token，返回用户名
      */
    public String getSubjectFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)//设置解析秘钥
                .build()
                .parseClaimsJws(token)//校验签名
                .getBody();//获取主体字段
        return claims.getSubject();// 返回主题字段
    }

    // 校验token是否有效
    public boolean validateToken(String token) {
        try {
            //进行签名校验
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
            //如果失败
        } catch (JwtException | IllegalArgumentException e) {
            // token无效或者过期
            return false;
        }
    }
}
