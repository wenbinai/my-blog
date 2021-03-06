package edu.nefu.myblog.util;

/**
 * 常用的常量
 */
public interface Constants {
    interface Page {
        int DEFAULT_PAGE = 1;
        int DEFAULT_SIZE = 10;
    }

    interface User {
        // 管理员角色
        String ROLE_ADMIN = "role_admin";
        // 普通用户角色
        String ROLE_NORMAL = "role_normal";
        // 默认头像地址
        String DEFAULT_AVATAR = "https://images.nowcoder.com/images/20191230/999991366_1577689271466_70A4DF560673F38B99D9AF985B5504D7?x-oss-process=image/resize,m_mfit,h_200,w_200";
        // 默认正常状态
        String DEFAULT_STATE = "1";
        // 图灵验证码内容
        String KEY_CAPTCHA_CONTENT = "key_captcha_content_";
        // 邮箱验证码内容
        String KEY_EMAIL_CONTENT = "key_email_content_";
        // 请求邮箱验证码方的IP
        String KEY_EMAIL_SEND_IP = "key_email_send_ip_";
        // 发送邮箱的目的地的邮箱地址
        String KEY_EMAIL_SEND_ADDRESS = "key_email_send_address_";
        // TOKEN key
        String KEY_TOKEN = "key_token_";
        // COOKIE key
        String KEY_COOKIE = "sob_blog_token";
    }

    interface Settings {
        // 管理员初始化状态
        String MANAGER_ACCOUNT_INIT_STATE = "init_state";
        // 发送邮件的标题
        String EMAIL_TITLE = "博客系统验证码";
    }

    // 单位位s
    interface TimeValueInSecond {
        int ONE_HOUR = 60 * 60;
        int TWO_HOUR = 60 * 60 * 2;
        int ONE_MONTH = 60 * 60 * 24 * 30;
        int HALF_MINUTE = 30;
        int TEN_MINUTE = 10 * 60;
    }

    // 单位毫秒
    interface TimeValueInMillion {
        long ONE_HOUR = 60 * 60 * 1000;
        long TWO_HOUR = 60 * 60 * 2 * 1000;
        long ONE_MONTH = 60 * 60 * 24 * 30 * 1000;
        long HALF_MINUTE = 30 * 1000;
        long TEN_MINUTE = 10 * 60 * 1000;
    }

    interface ImageType {
        String PREFIX = "image/";
        String TYPE_JPG = "jpg";
        String TYPE_PNG = "png";
        String TYPE_GIF = "gif";

        String TYPE_JPG_WITH_PREFIX = PREFIX + "jpg";
        String TYPE_GIF_WITH_PREFIX = PREFIX + "gif";
        String TYPE_PNG_WITH_PREFIX = PREFIX + "png";
    }


}
