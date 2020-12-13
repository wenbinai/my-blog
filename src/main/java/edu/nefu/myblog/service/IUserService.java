package edu.nefu.myblog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    void sendCaptchaCode(String captchaKey, HttpServletResponse response) throws Exception;

    ResponseResult getUserInfo(String userId) throws JsonProcessingException;

    ResponseResult updateUserInfo(HttpServletRequest request,
                                  HttpServletResponse response,
                                  String userId,
                                  User user);

    User checkUser(HttpServletRequest request, HttpServletResponse response);
}
