package com.mmall.controller.Manager;

import com.mmall.common.Const;
import com.mmall.common.ReturnResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/manager")
public class UserManagerController {
    @Autowired
    UserService userService;

    @RequestMapping("/login")
    @ResponseBody
    public ReturnResponse<User> login(HttpSession session, String username, String password) {
        ReturnResponse<User> returnResponse = userService.login(username, password);
        if (returnResponse.isSuccess()) {
            int role = returnResponse.getData().getRole();
            if (role != Const.SUPERUSER) return ReturnResponse.ReturnErrorByMessage("登陆账户非管理员");
            session.setAttribute(Const.USER, returnResponse.getData());
            return ReturnResponse.ReturnSuccess("管理员登陆成功", returnResponse.getData());
        }
        return returnResponse;
    }
}
