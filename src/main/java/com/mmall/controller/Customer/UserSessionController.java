package com.mmall.controller.Customer;

import com.mmall.common.Const;
import com.mmall.common.ReturnResponse;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/springSession")
public class UserSessionController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    @ResponseBody
    public ReturnResponse<User> login(String username, String password, HttpSession httpSession) {

        ReturnResponse<User> returnResponse = userService.login(username, password);
        if (returnResponse.isSuccess()) {
            httpSession.setAttribute(Const.USER, returnResponse.getData());
        }
        return returnResponse;
    }

    @RequestMapping(value = "/logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ReturnResponse<String> logout(HttpSession httpSession) {

        httpSession.removeAttribute(Const.USER);
        return ReturnResponse.ReturnErrorByMessage("成功登出");
    }

    @RequestMapping(value = "/get_information.do", method = RequestMethod.GET)
    @ResponseBody
    public ReturnResponse<User> getInformation(HttpSession httpSession) {

        User user = (User)httpSession.getAttribute(Const.USER);
        if (user != null) return ReturnResponse.ReturnSuccessByData(user);
        return ReturnResponse.ReturnErrorByMessage("获取用户信息失败");
    }
}
