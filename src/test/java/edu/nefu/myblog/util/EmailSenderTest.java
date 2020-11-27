package edu.nefu.myblog.util;

import javax.mail.MessagingException;

public class EmailSenderTest {
    public static void main(String[] args) throws MessagingException {
        EmailSender.subject("测试邮件发送")
                .from("AiWenbin")
                .text("邮箱验证码: 111111")
                .to("2086176146@qq.com")
                .send();
    }
}
