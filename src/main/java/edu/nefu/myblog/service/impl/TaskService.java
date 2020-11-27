package edu.nefu.myblog.service.impl;

import edu.nefu.myblog.util.EmailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class TaskService {
    // TODO 异步发送邮件待完成
    @Async
    public void asyncSendEmailVerifyCode(String verifyCode, String emailAddress) throws MessagingException {
        EmailSender.sendEmailVerifyCode(emailAddress, verifyCode);
    }
}
