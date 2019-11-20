package com.mmall.controller.Manager;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ReturnResponse;
import com.mmall.common.StatusCode;
import com.mmall.pojo.User;
import com.mmall.service.OrderService;
import com.mmall.service.UserService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisShardedUtil;
import com.mmall.vo.OrderVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manager/order")
public class OrderManagerController {

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @RequestMapping("/orderList.do")
    @ResponseBody
    public ReturnResponse<PageInfo> getOrderList(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }

        if (userService.checkSuperUserRole(user).isSuccess()) {
            return orderService.orderList(null, pageNum, pageSize);
        } else {
            return ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("/orderDetail.do")
    @ResponseBody
    public ReturnResponse<OrderVO> getOrderDetail(HttpServletRequest request, Long orderNum) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }

        if (userService.checkSuperUserRole(user).isSuccess()) {
            return orderService.getOrderDetailManage(orderNum);
        } else {
            return ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("/searchOrder.do")
    @ResponseBody
    public ReturnResponse<PageInfo> searchOrder(HttpServletRequest request, Long orderNum, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }

        if (userService.checkSuperUserRole(user).isSuccess()) {
            return orderService.searchOrderManage(orderNum, pageNum, pageSize);
        } else {
            return ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("/sendGoods.do")
    @ResponseBody
    public ReturnResponse<String> sendGoods(HttpServletRequest request, Long orderNum) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }

        if (userService.checkSuperUserRole(user).isSuccess()) {
            return orderService.sendGoods(orderNum);
        } else {
            return ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限");
        }
    }
}
