package com.mmall.controller.Customer;

import com.mmall.common.Const;
import com.mmall.common.ReturnResponse;
import com.mmall.common.StatusCode;
import com.mmall.pojo.User;
import com.mmall.service.UserService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisShardedUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    @ResponseBody
    public ReturnResponse<User> login(String username, String password, HttpSession httpSession, HttpServletResponse response) throws Exception{
        ReturnResponse<User> returnResponse = userService.login(username, password);
        if (returnResponse.isSuccess()) {
            CookieUtil.writeLoginToken(response, httpSession.getId());
            RedisShardedUtil.setEx(httpSession.getId(), JsonUtil.obj2String(returnResponse.getData()), Const.expireTime.sessionExpireTime);
        }
        return returnResponse;
    }

    @RequestMapping(value = "/logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = CookieUtil.readLoginToken(request);
        CookieUtil.delLoginToken(request, response);
        RedisShardedUtil.del(token);
        return ReturnResponse.ReturnErrorByMessage("成功登出");
    }

    @RequestMapping(value = "/register.do", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<String> register(User user) {
        return userService.register(user);
    }

    @RequestMapping(value = "/forget_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<String> forgetQuestion(String username) {
        return userService.forgetQuestion(username);
    }

    @RequestMapping(value = "/check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<String> checkAnswer(String username, String question, String answer) {
        return userService.checkAnswer(username, question, answer);
    }

    @RequestMapping(value = "/update_password_logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<String> updatePassword(String username, String passwordNew, String token) {
        return userService.updatePassword(username, passwordNew, token);
    }

    @RequestMapping(value = "/update_password_login.do", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<String> updatePasswordLogin(HttpServletRequest request, String passwordOld, String passwordNew) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) return ReturnResponse.ReturnErrorByMessage("您未登陆，请先登陆");
        return userService.updatePwdWhileLogin(user, passwordOld, passwordNew);
    }

    @RequestMapping(value = "/update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<User> updateInformation(HttpServletRequest request, User user) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User cUser = JsonUtil.string2Obj(userInfo, User.class);
        if (cUser == null) return ReturnResponse.ReturnErrorByMessage("您未登陆，请先登陆");
        if (cUser.getEmail().equals(user.getEmail())) return ReturnResponse.ReturnErrorByMessage("您正在使用此邮箱，请换成另外一个邮箱");
        user.setId(cUser.getId());
        user.setUsername(cUser.getUsername());
        ReturnResponse<User> returnResponse = userService.updateInformation(user);
        if (returnResponse.isSuccess()) {
            RedisShardedUtil.setEx(cookie, JsonUtil.obj2String(returnResponse.getData()), Const.expireTime.sessionExpireTime);
        }
        return returnResponse;
    }

    @RequestMapping(value = "/get_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ReturnResponse<User> getInformation(HttpServletRequest request) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user != null) return ReturnResponse.ReturnSuccessByData(user);
        return ReturnResponse.ReturnErrorByMessage("获取用户信息失败");
    }
}
