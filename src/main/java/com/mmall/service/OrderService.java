package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ReturnResponse;
import com.mmall.vo.OrderProductVO;
import com.mmall.vo.OrderVO;

import java.util.Map;

public interface OrderService {

    ReturnResponse payOrder(Integer userId, String path, Long orderNum);

    ReturnResponse aliPayCallback(Map<String, String> res);

    ReturnResponse queryOrderStatus(Integer userId, Long orderNum);

    ReturnResponse createOrder(Integer userId, Integer shippingId);

    ReturnResponse<String> cancelOrder(Integer userId, Long orderNum);

    ReturnResponse<OrderProductVO> getOrderProducts(Integer userId);

    ReturnResponse<OrderVO> getOrderDetail(Integer userId, Long orderNum);

    ReturnResponse<PageInfo> orderList(Integer userId, int pageNum, int pageSize);

    ReturnResponse<OrderVO> getOrderDetailManage(Long orderNum);

    ReturnResponse<PageInfo> searchOrderManage(Long orderNum, int pageNum, int pageSize);

    ReturnResponse<String> sendGoods(Long orderNum);
}
