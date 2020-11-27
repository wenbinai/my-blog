package edu.nefu.myblog.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对字符串进行判空,
 * 邮箱地址验证等
 * 等处理工具类
 */
public class TextUtil {
    /**
     * 字符串进行判空处理
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }

        return false;
    }

    /**
     * 校验邮箱地址格式
     *
     * @param emailAddress
     * @return
     */
    public static boolean isEmailAddress(String emailAddress) {
        String regEx = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(emailAddress);
        if (m.find()) {
            return true;
        }

        return false;
    }
}
