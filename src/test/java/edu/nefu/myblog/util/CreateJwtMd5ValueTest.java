package edu.nefu.myblog.util;

import org.springframework.util.DigestUtils;

public class CreateJwtMd5ValueTest {
    public static void main(String[] args) {
        String jwtKeyMd5Str = DigestUtils.md5DigestAsHex("my_blog_aiwenbin".getBytes());
        System.out.println(jwtKeyMd5Str);
    }
}
