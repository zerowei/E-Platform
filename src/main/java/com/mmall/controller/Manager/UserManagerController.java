package com.mmall.controller.Manager;

import com.mmall.common.Const;
import com.mmall.common.ReturnResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisShardedUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManagerController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    @ResponseBody
    public ReturnResponse<User> login(HttpSession session, HttpServletResponse response, String username, String password) throws Exception{
        ReturnResponse<User> returnResponse = userService.login(username, password);
        if (returnResponse.isSuccess()) {
            int role = returnResponse.getData().getRole();
            if (role != Const.SUPERUSER) return ReturnResponse.ReturnErrorByMessage("登陆账户非管理员");
            CookieUtil.writeLoginToken(response, session.getId());
            RedisShardedUtil.setEx(session.getId(), JsonUtil.obj2String(returnResponse.getData()), Const.expireTime.sessionExpireTime);
            return ReturnResponse.ReturnSuccess("管理员登陆成功", returnResponse.getData());
        }
        return returnResponse;
    }
}
