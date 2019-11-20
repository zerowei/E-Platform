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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/springSession")
public class UserSessionController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login.do", method = RequestMethod.GET)
    @ResponseBody
    public ReturnResponse<User> login(String username, String password, HttpSession httpSession, HttpServletResponse response) throws Exception{
        ReturnResponse<User> returnResponse = userService.login(username, password);
        if (returnResponse.isSuccess()) {
            httpSession.setAttribute(Const.USER, returnResponse.getData());
//            CookieUtil.writeLoginToken(response, httpSession.getId());
//            RedisShardedUtil.setEx(httpSession.getId(), JsonUtil.obj2String(returnResponse.getData()), Const.expireTime.sessionExpireTime);
        }
        return returnResponse;
    }

    @RequestMapping(value = "/logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ReturnResponse<String> logout(HttpSession httpSession, HttpServletRequest request, HttpServletResponse response) {
        httpSession.removeAttribute(Const.USER);
//        String token = CookieUtil.readLoginToken(request);
//        CookieUtil.delLoginToken(request, response);
//        RedisShardedUtil.del(token);
        return ReturnResponse.ReturnErrorByMessage("成功登出");
    }

    @RequestMapping(value = "/get_information.do", method = RequestMethod.GET)
    @ResponseBody
    public ReturnResponse<User> getInformation(HttpSession httpSession, HttpServletRequest request) {
//        String cookie = CookieUtil.readLoginToken(request);
//        if (StringUtils.isEmpty(cookie)) {
//            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
//        }
//        String userInfo = RedisShardedUtil.get(cookie);
//        User user = JsonUtil.string2Obj(userInfo, User.class);
        User user = (User)httpSession.getAttribute(Const.USER);
        if (user != null) return ReturnResponse.ReturnSuccessByData(user);
        return ReturnResponse.ReturnErrorByMessage("获取用户信息失败");
    }
}
