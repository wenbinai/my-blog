package edu.nefu.myblog.util;

import edu.nefu.myblog.pojo.User;
import io.jsonwebtoken.Claims;

import java.util.HashMap;
import java.util.Map;

public class ClaimsUtils {
    /**
     * 将User转化为JwtJson
     *
     * @param userFromDb
     * @return
     */
    public static Map<String, Object> User2Claims(User userFromDb) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userFromDb.getId());
        claims.put("user_name", userFromDb.getUserName());
        claims.put("roles", userFromDb.getRoles());
        claims.put("avatar", userFromDb.getAvatar());
        claims.put("email", userFromDb.getEmail());
        claims.put("sign", userFromDb.getSign());

        return claims;
    }

    /**
     * 将jwtJson转换为User
     *
     * @param claims
     * @return
     */
    public static User Claims2User(Claims claims) {
        User user = new User();
        String id = (String) claims.get("id");
        user.setId(id);
        String userName = (String) claims.get("user_name");
        user.setUserName(userName);
        String roles = (String) claims.get("roles");
        user.setRoles(roles);
        String avatar = (String) claims.get("avatar");
        user.setAvatar(avatar);
        String email = (String) claims.get("email");
        user.setEmail(email);
        String sign = (String) claims.get("sign");
        user.setSign(sign);

        return user;
    }
}
