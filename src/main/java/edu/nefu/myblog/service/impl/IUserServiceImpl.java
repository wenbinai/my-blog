package edu.nefu.myblog.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import edu.nefu.myblog.dao.RefreshTokenDao;
import edu.nefu.myblog.dao.SettingDao;
import edu.nefu.myblog.dao.UserDao;
import edu.nefu.myblog.pojo.RefreshToken;
import edu.nefu.myblog.pojo.Setting;
import edu.nefu.myblog.pojo.User;
import edu.nefu.myblog.response.ResponseResult;
import edu.nefu.myblog.service.IUserService;
import edu.nefu.myblog.util.*;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@Service
@Transactional
@Slf4j
public class IUserServiceImpl implements IUserService {
    @Autowired
    private SnowflakeIdWorker idWorker;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SettingDao settingDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RefreshTokenDao refreshTokenDao;

    public static final int[] captcha_font_types = {
            Captcha.FONT_1,
            Captcha.FONT_2,
            Captcha.FONT_3,
            Captcha.FONT_4,
            Captcha.FONT_5,
            Captcha.FONT_6,
            Captcha.FONT_7,
            Captcha.FONT_8,
            Captcha.FONT_9,
            Captcha.FONT_10
    };

    /**
     * 发送图灵验证码
     *
     * @param captchaKey
     * @param response
     * @throws Exception
     */
    public void sendCaptchaCode(String captchaKey, HttpServletResponse response) throws Exception {
        // TODO 防止图灵验证码发送过于频繁

        // 将时间戳作为图灵验证码的key 保证唯一性
        if (TextUtil.isEmpty(captchaKey) || captchaKey.length() < 13) {
            log.info("图灵验证码检验错误");
            return;
        }
        long key = 0l;
        try {
            key = Long.parseLong(captchaKey);
        } catch (Exception e) {
            log.info("图灵验证码解析错误");
            return;
        }

        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        int captchaType = new Random().nextInt(3);
        log.info("captchaType ==>" + captchaType);
        Captcha targetCaptcha = null;
        if (captchaType == 0) {
            // 三个参数分别为宽、高、位数
            targetCaptcha = new SpecCaptcha(200, 60, 5);
        } else if (captchaType == 1) {
            // gif类型
            targetCaptcha = new GifCaptcha(130, 48);
        } else {
            //算术类型
            targetCaptcha = new ArithmeticCaptcha(130, 48);
            targetCaptcha.setLen(2);
        }
        log.info("targetCaptcha ==>" + targetCaptcha);

        // 设置字体
        targetCaptcha.setFont(new Random().nextInt(captcha_font_types.length));
        String content = targetCaptcha.text().toLowerCase();

        log.info("content==>" + content);

        // 保存到redis里面 10分钟有效
        redisUtil.set(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey, content, Constants.TimeValueInSecond.TEN_MINUTE);
        log.info("键==>" + Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        // 显示图片
        targetCaptcha.out(response.getOutputStream());
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    @Override
    public ResponseResult getUserInfo(String userId) throws JsonProcessingException {
//        1. 对userId进行检验;
        if (TextUtil.isEmpty(userId)) {
            return ResponseResult.FAILED("用户Id不能为空");
        }
//        2. 通过userId查找用户是否存在;
        User user = userDao.findOneById(userId);

//        3. 若不存在, 返回用户不存在信息;
        if (user == null) {
            return ResponseResult.FAILED("用户不存在");
        }
//        4. 存在, 则返回用户某些不敏感信息
        ObjectMapper mapper = new ObjectMapper();

        String userJson = mapper.writeValueAsString(user);
        log.info("userJson--> ", userJson);
        User newUser = mapper.readValue(userJson, User.class);
        newUser.setPassword("");
        newUser.setEmail("");
        newUser.setRegIp("");
        newUser.setLoginIp("");
        ResponseResult responseResult = ResponseResult.SUCCESS("获取成功");
        log.info("new User -->", newUser);
        responseResult.setData(newUser);
        return responseResult;
    }

    /**
     * 修改用户信息
     *
     * @param request
     * @param response
     * @param userId
     * @param user
     * @return
     */
    @Override
    public ResponseResult updateUserInfo(HttpServletRequest request, HttpServletResponse response, String userId, User user) {
//        修改用户需要用户已经登陆的权限(通过jwt中的token);
        User userFromTokenKey = checkUser();
        if (userFromTokenKey == null) {
            return ResponseResult.ACCOUNT_NOT_LOGIN();
        }
        User userFromDb = userDao.findOneById(userFromTokenKey.getId());
//        1. 通过userId判断用户;
        if (!userFromDb.getId().equals(userId)) {
            return ResponseResult.PERMISSION_FORBID();
        }
//        2. 修改用户名;
        if (!TextUtil.isEmpty(user.getUserName())) {
            User oneByUserName = userDao.findOneByUserName(user.getUserName());
            if (oneByUserName != null) {
                return ResponseResult.FAILED("用户名已注册");
            }
            userFromDb.setUserName(user.getUserName());
        }
//        3. 修改头像;
        if (!TextUtil.isEmpty(user.getAvatar())) {
            userFromDb.setAvatar(user.getAvatar());
        }
//        4. 修改签名;
        userFromDb.setSign(user.getSign());
        userDao.save(userFromDb);
//        5. 删除token, 下次需要token时, 会根据refreshToken从新创建
        String tokenKey = CookieUtil.getCookie(request, Constants.User.KEY_TOKEN);
        log.info("tokenKey ==>" + tokenKey);
        redisUtil.del(Constants.User.KEY_TOKEN + tokenKey);
        log.info("redis 删除tokenKey==>" + Constants.User.KEY_TOKEN + tokenKey);
        return ResponseResult.SUCCESS("用户信息更新成功");
    }


    /**
     * 校验用户是否登陆
     *
     * @return
     */
    @Override
    public User checkUser() {
        String tokenKey = CookieUtil.getCookie(getRequest(), Constants.User.KEY_TOKEN);
        log.info("获取userInfo==>" + tokenKey);

        User user = parseToken(tokenKey);
        if (user == null) {
            RefreshToken refreshToken = refreshTokenDao.findOneByTokenKey(tokenKey);
            // 用户没有登陆否则refreshToken过期
            if (refreshToken == null) {
                return null;
            }
            String userId = refreshToken.getUserId();
            log.info("userId==>" + userId);
            User userFromDB = userDao.findOneById(userId);
            String newTokenKey = createToken(getResponse(), userFromDB);
            log.info("newTokenKey==>" + newTokenKey);
            return parseToken(newTokenKey);
        }
        return user;
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return servletRequestAttributes.getRequest();
    }

    private HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return servletRequestAttributes.getResponse();

    }

    private User parseToken(String tokenKey) {

        String jwtStr = (String) redisUtil.get(Constants.User.KEY_TOKEN + tokenKey);
        log.info("获取jwtStr==>" + jwtStr);
        // tokenKey 无效或者没有
        if (TextUtil.isEmpty(jwtStr)) {
            return null;
        }
        Claims claims = JwtUtil.parseJWT(jwtStr);
        User user = ClaimsUtils.Claims2User(claims);
        log.info("claim to user ==>" + user.toString());
        return user;
    }

    /**
     * 发送邮箱验证码
     *
     * @param emailAddress
     * @param request
     * @return
     */
    @Override
    public ResponseResult sendVerifyCode(String emailAddress, HttpServletRequest request) {
        // 获取ip地址(代理地址也包括)
        String remoteAddr = request.getRemoteAddr();
        log.info("sendEmail ==> ip ==> " + remoteAddr);
        // ip地址格式转化, 防止redis中嵌套
        if (remoteAddr != null) {
            remoteAddr = remoteAddr.replaceAll(":", "_");
        }
        log.info("send_ip ==> " + remoteAddr);
        // 验证该地址在一个小时内发送的次数
        Integer ipSendTime = (Integer) redisUtil.get(Constants.User.KEY_EMAIL_SEND_IP + remoteAddr);
        log.info("同一ip地址发送次数-->" + ipSendTime);
        if (ipSendTime != null && ipSendTime > 10) {
            return ResponseResult.FAILED("你发送验证码也太频繁了吧!");
        }
        // 验证目标邮箱在30s内发送次数
        Object addressSendTime = redisUtil.get(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress);
        log.info("同一邮箱30s内发送次数-->" + addressSendTime);
        if (addressSendTime != null) {
            return ResponseResult.FAILED("你发送的验证码也太频繁了吧!");
        }
        // 1. 对邮箱进行判空
        if (TextUtil.isEmpty(emailAddress)) {
            return ResponseResult.FAILED("邮箱地址不能为空");
        }
        // 2. 对邮箱格式进行校验
        if (!TextUtil.isEmailAddress(emailAddress)) {
            return ResponseResult.FAILED("邮箱地址格式不正确");
        }
        // 4. 随机生成6位邮箱验证码
        String verifyCode = randomCode();
        log.info("邮箱验证码-->" + verifyCode);

        // 5. 发送邮箱验证码
        try {
            // 异步发送
            taskService.asyncSendEmailVerifyCode(verifyCode, emailAddress);
        } catch (MessagingException e) {
            log.info(e.toString());
            return ResponseResult.FAILED("发送邮箱验证码失败");
        }
        //6. 做记录
        if (ipSendTime == null) {
            ipSendTime = 0;
        }
        ipSendTime++;
        // 1个小时有效期
        redisUtil.set(Constants.User.KEY_EMAIL_SEND_IP + remoteAddr, ipSendTime, Constants.TimeValueInSecond.ONE_HOUR);
        // 30秒内不能重发
        redisUtil.set(Constants.User.KEY_EMAIL_SEND_ADDRESS + emailAddress, "true", Constants.TimeValueInSecond.HALF_MINUTE);
        // 保存邮箱验证码到redis中 10分钟有效期
        redisUtil.set(Constants.User.KEY_EMAIL_CONTENT + emailAddress, verifyCode, Constants.TimeValueInSecond.TEN_MINUTE);

        return ResponseResult.SUCCESS("发送邮箱验证码成功, 请在有限期内填写");
    }


    /**
     * 生成随机6位验证码
     */
    private static String randomCode() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }

    /**
     * 初始化管理员账号
     *
     * @param user
     * @param request
     * @return
     */
    @Override
    public ResponseResult initManagerAccount(User user, HttpServletRequest request) {
        // 0. 检查是否初始化
        Setting managerAccountState = settingDao.findOneByKeyLabel(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        if (managerAccountState != null) {
            return ResponseResult.FAILED("管理员账号已经初始化");
        }
        // 1. TODO 利用常用的设计模式减少if-else数量 检查数据
        if (TextUtil.isEmpty(user.getUserName())) {
            return ResponseResult.FAILED("用户名不能为空 ");
        }
        if (TextUtil.isEmpty(user.getPassword())) {
            return ResponseResult.FAILED("密码不能为空");
        }
        if (TextUtil.isEmpty(user.getEmail())) {
            return ResponseResult.FAILED("邮箱不能为空");
        }
        // 2. 补充用户数据
        user.setId(String.valueOf(idWorker.nextId()));
        user.setRoles(Constants.User.ROLE_ADMIN);
        user.setAvatar(Constants.User.DEFAULT_AVATAR);
        user.setState(Constants.User.DEFAULT_STATE);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // 获取登陆的ip地址
        String remoteAddr = request.getRemoteAddr();
        log.info("remoteAddr ==>" + remoteAddr);
        user.setRegIp(remoteAddr);
        user.setLoginIp(remoteAddr);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        // 3. 将数据插入数据库中
        userDao.save(user);
        log.info("插入用户表成功");
        // 4. 更新已经初始化管理员账号的标记
        Setting setting = new Setting();
        setting.setId(String.valueOf(idWorker.nextId()));
        setting.setKeyLabel(Constants.Settings.MANAGER_ACCOUNT_INIT_STATE);
        setting.setCreateTime(new Date());
        setting.setUpdateTime(new Date());
        setting.setValue("1");
        log.info("setting-->" + setting.toString());
        settingDao.save(setting);
        log.info("插入设置表成功");
        return ResponseResult.SUCCESS("初始化管理员账号成功");
    }


    /**
     * 用户注册
     *
     * @param user
     * @param emailCode
     * @param captchaCode
     * @param captchaKey
     * @param request
     * @return
     */
    @Override
    public ResponseResult register(User user, String emailCode, String captchaCode, String captchaKey, HttpServletRequest request) {
        // 1. 检查用户是否注册
        String userName = user.getUserName();
        if (TextUtil.isEmpty(userName)) {
            return ResponseResult.FAILED("用户姓名不能为空");
        }
        User userByName = userDao.findOneByUserName(userName);
        if (userByName != null) {
            return ResponseResult.FAILED("用户名已注册");
        }
        // 2. 检验邮箱
        String email = user.getEmail();
        if (TextUtil.isEmpty(email)) {
            return ResponseResult.FAILED("邮箱地址不能为空");
        }
        // 检查邮箱格式是否正确待完成
        if (!TextUtil.isEmailAddress(email)) {
            return ResponseResult.FAILED("邮箱地址格式不正确");
        }
        // 3. 检查邮箱是否注册过
        User userByEmail = userDao.findOneByEmail(email);
        if (userByEmail != null) {
            return ResponseResult.FAILED("该邮箱地址已经注册");
        }
        // 4. 检查邮箱验证码是否正确
        String emailVerifyCode = (String) redisUtil.get(Constants.User.KEY_EMAIL_CONTENT + email);
        if (TextUtil.isEmpty(emailVerifyCode)) {
            return ResponseResult.FAILED("邮箱验证码已过期");
        }
        if (!emailVerifyCode.equals(emailCode)) {
            return ResponseResult.FAILED("邮箱验证码不正确");
        } else {
            // 如果正确, 删除redis中的邮箱验证码
            redisUtil.del(Constants.User.KEY_EMAIL_CONTENT + email);
        }
        // 5. 检查图灵验证码是否正确
        String captchaVerifyCode = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        if (TextUtil.isEmpty(captchaVerifyCode)) {
            return ResponseResult.FAILED("验证码已过期, 请重新刷新");
        }
        if (!captchaVerifyCode.equals(captchaCode)) {
            return ResponseResult.FAILED("验证码不正确");
        } else {
            // 如果正确
            redisUtil.del(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        }
        // 6. 对密码进行加密
        String password = user.getPassword();
        if (TextUtil.isEmpty(password)) {
            return ResponseResult.FAILED("密码不可以为空");
        }
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        // 7. 补全数据
        String ipAddress = request.getRemoteAddr();
        user.setRegIp(ipAddress);
        user.setLoginIp(ipAddress);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setAvatar(Constants.User.DEFAULT_AVATAR);
        user.setRoles(Constants.User.ROLE_NORMAL);
        user.setState("1");
        user.setId(idWorker.nextId() + "");
        // 8. 将用户数据插入到数据库中
        userDao.save(user);
        return ResponseResult.SUCCESS("注册成功");
    }

    /**
     * 用户登陆
     *
     * @param captcha
     * @param captchaKey
     * @param user
     * @param request
     * @param response
     * @return
     */
    @Override
    public ResponseResult login(String captcha, String captchaKey, User user, HttpServletRequest request, HttpServletResponse response) {
        log.info("后面键==>" + Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        String captchaValue = (String) redisUtil.get(Constants.User.KEY_CAPTCHA_CONTENT + captchaKey);
        log.info("captchaValue==>" + captcha);
        if (TextUtil.isEmpty(captchaValue)) {
            return ResponseResult.FAILED("验证码已过期");
        }
        if (!captchaValue.equals(captcha)) {
            return ResponseResult.FAILED("验证码错误");
        }

        String userName = user.getUserName();
        if (TextUtil.isEmpty(userName)) {
            return ResponseResult.FAILED("账号不能为空");
        }
        String password = user.getPassword();
        if (TextUtil.isEmpty(password)) {
            return ResponseResult.FAILED("密码不能为空");
        }

        // TODO 增加输入邮箱也可以登陆逻辑
        User userFromDb = userDao.findOneByUserName(userName);
        if (userFromDb == null) {
            return ResponseResult.FAILED("用户名或密码不正确");
        }

        /**
         * 前端密码加密怎么做?
         */
        boolean matches = bCryptPasswordEncoder.matches(password, userFromDb.getPassword());
        if (!matches) {
            return ResponseResult.FAILED("用户名或密码不正确");
        }

        // 判断用户状态
        if (!"1".equals(userFromDb.getState())) {
            return ResponseResult.FAILED("当前帐号被禁用");
        }

        // TODO 创建token
        createToken(response, userFromDb);
        return ResponseResult.SUCCESS("登陆成功");
    }

    /**
     * 创建refreshToken
     *
     * @param response
     * @param userFromDb
     * @return
     */
    private String createToken(HttpServletResponse response, User userFromDb) {
        // 删除之前存在数据库中的refresh_token
        int deleteResult = refreshTokenDao.deleteAllByUserId(userFromDb.getId());
        log.info("deleteResult of refresh token .. " + deleteResult);
        //生成token
        Map<String, Object> claims = ClaimsUtils.User2Claims(userFromDb);
        //token默认有效为2个小时
        String token = JwtUtil.createToken(claims);
        //返回token的md5值，token会保存到redis里
        //前端访问的时候，携带token的md5key，从redis中获取即可
        String tokenKey = DigestUtils.md5DigestAsHex(token.getBytes());
        //保存token到redis里，有效期为2个小时，key是tokenKey
        redisUtil.set(Constants.User.KEY_TOKEN + tokenKey, token, Constants.TimeValueInMillion.TWO_HOUR);

        //把tokenKey写到cookies里
        CookieUtil.setUpCookie(response, Constants.User.KEY_TOKEN, tokenKey);
        //生成refreshToken, 保存一个月
        String refreshTokenValue = JwtUtil.createRefreshToken(userFromDb.getId(), Constants.TimeValueInMillion.ONE_MONTH);
        //保存到数据库里
        //refreshToken，tokenKey，用户ID，创建时间，更新时间
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(idWorker.nextId() + "");
        refreshToken.setRefreshToken(refreshTokenValue);
        refreshToken.setUserId(userFromDb.getId());
        refreshToken.setTokenKey(tokenKey);
        refreshToken.setCreateTime(new Date());
        refreshToken.setUpdateTime(new Date());
        refreshTokenDao.save(refreshToken);
        return tokenKey;
    }

}
