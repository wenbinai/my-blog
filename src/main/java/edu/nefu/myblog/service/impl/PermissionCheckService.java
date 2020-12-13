package edu.nefu.myblog.service.impl;

import edu.nefu.myblog.pojo.User;
import edu.nefu.myblog.service.IUserService;
import edu.nefu.myblog.util.Constants;
import edu.nefu.myblog.util.CookieUtil;
import edu.nefu.myblog.util.TextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service("permission")
public class PermissionCheckService {
    @Autowired
    private IUserService userService;


    public boolean adminPermission() {
        // 获取到当前权限所有的角色，进行角色对比即可确定权限
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        String token = CookieUtil.getCookie(request, Constants.User.KEY_TOKEN);
        log.info("token-->" + token);

        if (TextUtil.isEmpty(token)) {
            return false;
        }
        User user = userService.checkUser(request, response);
        log.info("user==>" + user.toString());
        if (user == null || TextUtil.isEmpty(user.getRoles())) {
            return false;
        }

        if (Constants.User.ROLE_ADMIN.equals(user.getRoles())) {
            return true;
        }
        log.info("before return false!");
        return false;
    }
}
