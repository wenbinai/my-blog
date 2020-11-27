package edu.nefu.myblog.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {
    public static final int DEFAULT_AGE = 60 * 60 * 24 * 365;

    public static final String DEFAULT_DOMAIN = "localhost";

    public static void setUpCookie(HttpServletResponse response, String key, String value) {
        setUpCookie(response, key, value, DEFAULT_AGE);
    }

    public static void setUpCookie(HttpServletResponse response, String key, String value, int age) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setDomain(DEFAULT_DOMAIN);
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletResponse response, String key) {
        setUpCookie(response, key, null, 0);
    }

    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (key.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }


}
