package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ReturnResponse;
import com.mmall.pojo.Shipping;

public interface ShippingService {

    ReturnResponse addAddress(Integer userId, Shipping shipping);

    ReturnResponse deleteAddress(Integer userId, Integer shippingId);

    ReturnResponse updateAddress(Integer userId, Shipping shipping);

    ReturnResponse<Shipping> searchAddress(Integer userId, Integer shippingId);

    ReturnResponse<PageInfo> addressList(Integer userId, Integer pageNum, Integer pageSize);
}
