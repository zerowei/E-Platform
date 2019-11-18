package com.mmall.service;

import com.mmall.common.ReturnResponse;
import com.mmall.vo.CartListVO;

public interface CartService {

    ReturnResponse<CartListVO> addCart(Integer userId, Integer productId, Integer count);

    ReturnResponse<CartListVO> updateCart(Integer userId, Integer productId, Integer count);

    ReturnResponse<CartListVO> deleteCart(Integer userId, String productIds);

    ReturnResponse<CartListVO> searchCarts(Integer userId);

    ReturnResponse<CartListVO> allSelects(Integer userId, Integer productId, Integer checked);

    ReturnResponse<Integer> productQuantities(Integer userId);
}
