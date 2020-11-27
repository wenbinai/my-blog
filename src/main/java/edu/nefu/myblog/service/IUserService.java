package edu.nefu.myblog.service;

import edu.nefu.myblog.pojo.User;
import edu.nefu.myblog.response.ResponseResult;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IUserService {

    ResponseResult sendVerifyCode(String emailAddress, HttpServletRequest request);

    ResponseResult initManagerAccount(User user, HttpServletRequest request);

    ResponseResult register(User user, String emailCode,
                            String captchaCode, String captchaKey,
                            HttpServletRequest request);

    ResponseResult login(String captcha,
                         String captchakey,
                         User user,
                         HttpServletRequest request,
                         HttpServletResponse response);

    void sendCaptchaCode(String captchaKey, HttpServletResponse response) throws Exception ;
}
