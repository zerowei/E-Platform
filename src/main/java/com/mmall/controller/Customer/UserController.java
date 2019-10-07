package com.mmall.controller.Customer;

import com.mmall.common.Const;
import com.mmall.common.ReturnResponse;
import com.mmall.common.StatusCode;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<User> login(String username, String password, HttpSession httpSession) {
        ReturnResponse<User> returnResponse = userService.login(username, password);
        if (returnResponse.isSuccess()) {
            httpSession.setAttribute(Const.USER, returnResponse.getData());
        }
        return returnResponse;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<String> logout(HttpSession httpSession) {
        httpSession.removeAttribute(Const.USER);
        return ReturnResponse.ReturnErrorByMessage("成功登出");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<String> register(User user) {
        return userService.register(user);
    }

    @RequestMapping(value = "/forget_question", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<String> forgetQuestion(String username) {
        return userService.forgetQuestion(username);
    }

    @RequestMapping(value = "/check_answer", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<String> checkAnswer(String username, String question, String answer) {
        return userService.checkAnswer(username, question, answer);
    }

    @RequestMapping(value = "/update_password_logout", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<String> updatePassword(String username, String passwordNew, String token) {
        return userService.updatePassword(username, passwordNew, token);
    }

    @RequestMapping(value = "/update_password_login", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<String> updatePasswordLogin(HttpSession httpSession, String passwordOld, String passwordNew) {
        User user = (User)httpSession.getAttribute(Const.USER);
        if (user == null) return ReturnResponse.ReturnErrorByMessage("您未登陆，请先登陆");
        return userService.updatePwdWhileLogin(user, passwordOld, passwordNew);
    }

    @RequestMapping(value = "/update_information", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<User> updateInformation(HttpSession httpSession, User user) {
        User cUser = (User)httpSession.getAttribute(Const.USER);
        if (cUser == null) return ReturnResponse.ReturnErrorByMessage("您未登陆，请先登陆");
        if (cUser.getEmail().equals(user.getEmail())) return ReturnResponse.ReturnErrorByMessage("您正在使用此邮箱，请换成另外一个邮箱");
        user.setId(cUser.getId());
        user.setUsername(cUser.getUsername());
        ReturnResponse<User> returnResponse = userService.updateInformation(user);
        if (returnResponse.isSuccess()) {
            httpSession.setAttribute(Const.USER, returnResponse.getData());
        }
        return returnResponse;
    }

    @RequestMapping(value = "/get_information", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<User> getInformation(HttpSession session) {
        User user = (User)session.getAttribute(Const.USER);
        if (user == null) return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        return userService.getInformation(user.getId());
    }
}
