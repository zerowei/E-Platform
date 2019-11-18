package com.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ReturnResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("ShippingService")
public class ShippingServiceImpl implements ShippingService {

    @Autowired
    ShippingMapper shippingMapper;

    public ReturnResponse addAddress(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int count = shippingMapper.insert(shipping);
        if (count > 0) {
            Map<String, Integer> shippingId = new HashMap<>();
            shippingId.put("shippingId", shipping.getId());
            return ReturnResponse.ReturnSuccess("新增地址成功", shippingId);
        }
        return ReturnResponse.ReturnErrorByMessage("新增地址失败");
    }

    public ReturnResponse deleteAddress(Integer userId, Integer shippingId) {
        int count = shippingMapper.deleteByUserIdShippingId(userId, shippingId);
        if (count > 0) return ReturnResponse.ReturnSuccessByMessage("删除地址成功");
        return ReturnResponse.ReturnErrorByMessage("删除地址失败");
    }

    public ReturnResponse updateAddress(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int count = shippingMapper.updateByShipping(shipping);
        if (count > 0) return ReturnResponse.ReturnSuccessByMessage("更新地址成功");
        return ReturnResponse.ReturnErrorByMessage("更新地址失败");
    }

    public ReturnResponse<Shipping> searchAddress(Integer userId, Integer shippingId) {
        Shipping shipping = shippingMapper.searchByUserIdShippingId(userId, shippingId);
        if (shipping != null) return ReturnResponse.ReturnSuccess("查询地址成功", shipping);
        return ReturnResponse.ReturnErrorByMessage("查询地址失败");
    }

    public ReturnResponse<PageInfo> addressList(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Shipping> res = shippingMapper.addressList(userId);
        PageInfo pageInfo = new PageInfo(res);
        return ReturnResponse.ReturnSuccessByData(pageInfo);
    }
}
