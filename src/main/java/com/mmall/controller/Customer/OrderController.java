package com.mmall.controller.Customer;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ReturnResponse;
import com.mmall.common.StatusCode;
import com.mmall.pojo.User;
import com.mmall.service.OrderService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderService orderService;

    @RequestMapping("/payOrder.do")
    @ResponseBody
    public ReturnResponse payOrder(Long orderNum, HttpServletRequest request) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return orderService.payOrder(user.getId(), path, orderNum);
    }

    @RequestMapping("/createOrder.do")
    @ResponseBody
    public ReturnResponse createOrder(HttpServletRequest request, Integer shippingId) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        return orderService.createOrder(user.getId(), shippingId);
    }

    @RequestMapping("/cancelOrder.do")
    @ResponseBody
    public ReturnResponse cancelOrder(HttpServletRequest request, Long orderNum) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        return orderService.cancelOrder(user.getId(), orderNum);
    }

    @RequestMapping("/getOrderProducts.do")
    @ResponseBody
    public ReturnResponse getOrderProducts(HttpServletRequest request) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        return orderService.getOrderProducts(user.getId());
    }

    @RequestMapping("/getOrderDetail.do")
    @ResponseBody
    public ReturnResponse getOrderDetail(HttpServletRequest request, Long orderNum) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        return orderService.getOrderDetail(user.getId(), orderNum);
    }

    @RequestMapping("/orderList.do")
    @ResponseBody
    public ReturnResponse<PageInfo> orderList(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        return orderService.orderList(user.getId(), pageNum, pageSize);
    }

    @RequestMapping("/alipay_callback.do")
    @ResponseBody
    public Object aliPayCallback(HttpServletRequest request) {

        Map<String, String> res = new HashMap<>();
        Map<String, String[]> result = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : result.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                temp.append((i == values.length - 1) ? values[i] : values[i] + ",");
            }
            res.put(key, temp.toString());
        }
        logger.info("支付宝回调的trade_status为:{}，其他各种参数为：{}", res.get("trade_status"), res);
        res.remove("sign_type");
        try {
            boolean aliPayRSACheckRes = AlipaySignature.rsaCheckV2(res, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!aliPayRSACheckRes) {
                return ReturnResponse.ReturnErrorByMessage("签名校验失败，非法");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝签名校验异常", e);
        }

        ReturnResponse response = orderService.aliPayCallback(res);
        if (response.isSuccess()) {
            return Const.TradeStatus.SUCCESS;
        }
        return Const.TradeStatus.FAIL;
    }

    @RequestMapping("query_order_status.do")
    @ResponseBody
    public ReturnResponse<Boolean> queryOrderStatus(HttpServletRequest request, Long orderNum) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        ReturnResponse response = orderService.queryOrderStatus(user.getId(), orderNum);
        if (response.isSuccess()) {
            return ReturnResponse.ReturnSuccessByData(true);
        }
        return ReturnResponse.ReturnSuccessByData(false);
    }
}
