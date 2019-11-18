package com.mmall.controller.Customer;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ReturnResponse;
import com.mmall.common.StatusCode;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.ShippingService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    ShippingService shippingService;

    @RequestMapping("/addAddress.do")
    @ResponseBody
    public ReturnResponse addAddress(HttpServletRequest request, Shipping shipping) {

        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        return shippingService.addAddress(user.getId(), shipping);
    }

    @RequestMapping("/deleteAddress.do")
    @ResponseBody
    public ReturnResponse deleteAddress(HttpServletRequest request, Integer shippingId) {

        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        return shippingService.deleteAddress(user.getId(), shippingId);
    }

    @RequestMapping("/updateAddress.do")
    @ResponseBody
    public ReturnResponse updateAddress(HttpServletRequest request, Shipping shipping) {

        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        return shippingService.updateAddress(user.getId(), shipping);
    }

    @RequestMapping("/searchAddress.do")
    @ResponseBody
    public ReturnResponse<Shipping> searchAddress(HttpServletRequest request, Integer shippingId) {

        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        return shippingService.searchAddress(user.getId(), shippingId);
    }

    @RequestMapping("/addressList.do")
    @ResponseBody
    public ReturnResponse<PageInfo> addressList(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        return shippingService.addressList(user.getId(), pageNum, pageSize);
    }
}
