package edu.nefu.myblog.controller;

import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import edu.nefu.myblog.pojo.Comment;
import edu.nefu.myblog.pojo.Student;
import edu.nefu.myblog.pojo.User;
import edu.nefu.myblog.response.ResponseResult;
import edu.nefu.myblog.service.IUserService;
import edu.nefu.myblog.util.Constants;
import edu.nefu.myblog.util.JwtUtil;
import edu.nefu.myblog.util.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private IUserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/student")
    public ResponseResult getStudent() {
        Student student = new Student("张三", "男", 27);
        if (student != null) {
            return ResponseResult.SUCCESS();
        } else {
            return ResponseResult.FAILED();
        }
    }

//    @GetMapping("/user/{id}")
//    public User getUserById(@PathVariable("id") String id) {
//        return userService.findUserById(id);
//    }
//
//    @PostMapping("/user")
//    public String addUser(@RequestBody User user) {
//        int i = userService.addUser(user);
//        if (i == 0) {
//            return "添加成功";
//        } else {
//            return "添加失败";
//        }
//    }

    @PostMapping("/register")
    public ResponseResult register() {
        return ResponseResult.FAILED();
    }

    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException, FontFormatException {
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
        // 设置字体
        // specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        specCaptcha.setFont(Captcha.FONT_1);
        // 设置类型，纯数字、纯字母、字母数字混合
        //specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);

        String content = specCaptcha.text().toLowerCase();
        log.info("captcha content == > " + content);
        // 验证码存入session
        // request.getSession().setAttribute("captcha", content);

        //验证码存入到redis
        redisUtil.set(Constants.User.KEY_CAPTCHA_CONTENT + "123456", content, 60 * 10);

        // 输出图片流
        specCaptcha.out(response.getOutputStream());
    }

    @GetMapping("/{email}")
    public ResponseResult sendEmailVerifyCode(@PathVariable("email") String emailAddress, HttpServletRequest request) {
        ResponseResult responseResult = userService.sendVerifyCode(emailAddress, request);
        return responseResult;
    }

    @PostMapping("/comment")
    public void testComment(@RequestBody Comment commment, HttpServletRequest request) {
        String content = commment.getContent();
        log.info("comment content ==>" + content);
        // 对评论身份进行验证
        String tokenKey = getCookie(Constants.User.KEY_COOKIE, request);
        if (tokenKey == null) {
//            return ResponseResult.FAILED("账号未登录");
        }
        String token = (String) redisUtil.get(Constants.User.KEY_TOKEN + tokenKey);
        if (token == null) {
            //todo
        }

        // 已经登陆 补充信息
        Claims claims = JwtUtil.parseJWT(token);
        String userId = (String) claims.get("id");
        commment.setUserId(userId);

    }

    private String getCookie(String cookieKey, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookieKey.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
