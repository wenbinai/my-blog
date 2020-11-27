package edu.nefu.myblog.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
/**
 * 测试密码加密类的使用
 */
public class PasswordEncoderTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");

        log.info("长度-->" + encode.length());

        String originalPassword = "123456";
        log.info("是否相同-->" + passwordEncoder.matches(originalPassword, encode));

        originalPassword = "1234567";
        log.info("是否相同-->" + passwordEncoder.matches(originalPassword, encode));

    }
}
