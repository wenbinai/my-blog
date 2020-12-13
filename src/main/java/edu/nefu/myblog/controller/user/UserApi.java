package edu.nefu.myblog.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.nefu.myblog.pojo.User;
import edu.nefu.myblog.response.ResponseResult;
import edu.nefu.myblog.service.IUserService;
import edu.nefu.myblog.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserApi {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private IUserService userService;

    /**
     * 初始化管理员账号
     *
     * @param user
     * @return
     */
    @PostMapping("/admin_account")
    public ResponseResult initManagerAccount(@RequestBody User user, HttpServletRequest request) {
        return userService.initManagerAccount(user, request);
    }

    /**
     * 注册
     *
     * @param user
     * @return
     */
    @PostMapping
    public ResponseResult register(@RequestBody User user,
                                   @RequestParam("email_code") String emailCode,
                                   @RequestParam("captcha_code") String captchaCode,
                                   @RequestParam("captcha_key") String captchaKey,
                                   HttpServletRequest request) {
        return userService.register(user, emailCode, captchaCode, captchaKey, request);
    }

    /**
     * 登陆
     *
     * @param captcha
     * @param user
     * @return
     */
    @PostMapping("/{captcha}")
    public ResponseResult login(@PathVariable("captcha") String captcha,
                                @RequestBody User user,
                                @RequestParam("captcha_key") String captchaKey,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        return userService.login(captcha, captchaKey, user, request, response);
    }


    /**
     * 获取图灵验证码
     *
     * @return
     */
    @GetMapping(value = "/captcha", produces = MediaType.IMAGE_JPEG_VALUE)
    public void getCaptcha(HttpServletResponse response, @RequestParam("captcha_key") String captchaKey) throws Exception {
        try {
            userService.sendCaptchaCode(captchaKey, response);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("发送图灵验证码失败");
        }
    }

    /**
     * 发送邮件 email
     *
     * @param emailAddress
     * @return
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(@RequestParam("email_address") String emailAddress, HttpServletRequest request) {
        return userService.sendVerifyCode(emailAddress, request);
    }

    /**
     * 修改密码
     *
     * @param user
     * @return
     */
    @PutMapping("/password")
    public ResponseResult updatePassword(@RequestBody User user) {
        return null;
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId) throws JsonProcessingException {
        return userService.getUserInfo(userId);
    }

    /**
     * 修改用户信息
     *
     * @return
     */
    @PutMapping("/{userId}")
    public ResponseResult updateUserInfo(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable("userId") String userId,
            @RequestBody User user) {

        return userService.updateUserInfo(request, response, userId, user);
    }


}
