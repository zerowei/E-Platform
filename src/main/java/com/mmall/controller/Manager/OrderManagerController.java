package com.mmall.controller.Manager;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ReturnResponse;
import com.mmall.service.OrderService;
import com.mmall.service.UserService;
import com.mmall.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/manage/order")
public class OrderManagerController {

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    @RequestMapping("/orderList.do")
    @ResponseBody
    public ReturnResponse<PageInfo> getOrderList(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        return orderService.orderList(null, pageNum, pageSize);
    }

    @RequestMapping("/orderDetail.do")
    @ResponseBody
    public ReturnResponse<OrderVO> getOrderDetail(Long orderNum) {

        return orderService.getOrderDetailManage(orderNum);
    }

    @RequestMapping("/searchOrder.do")
    @ResponseBody
    public ReturnResponse<PageInfo> searchOrder(Long orderNum, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        return orderService.searchOrderManage(orderNum, pageNum, pageSize);
    }

    @RequestMapping("/sendGoods.do")
    @ResponseBody
    public ReturnResponse<String> sendGoods(Long orderNum) {

        return orderService.sendGoods(orderNum);
    }
}
