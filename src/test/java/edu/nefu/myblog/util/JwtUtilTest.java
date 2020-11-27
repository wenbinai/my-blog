package edu.nefu.myblog.util;

import io.jsonwebtoken.Claims;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;

public class JwtUtilTest {
    public static void main(String[] args) {
        Map<String, Object> claims = new ConcurrentHashMap<>();
        claims.put("id", "111");
        claims.put("name", "艾文斌");
        String token = JwtUtil.createToken(claims, 3 * 60 * 60 * 100);
        System.out.println(token);

        Claims claim = JwtUtil.parseJWT(token);
        System.out.println(claim.getId());
        System.out.println(claim.getSubject());
        System.out.println(claim.get("id"));
        System.out.println(claim.get("name"));
        System.out.println(claim);
    }
}
