package com.mmall.dao;

import com.mmall.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart checkCartByUserProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> getCartsByUserId(Integer userId);

    int isAllChecked(Integer userId);

    int deleteByProductIdList(@Param("userId")Integer userId, @Param("productIdList") List<Integer> productIdList);

    int allSelects(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checked") Integer checked);

    int productQuantities(Integer userId);

    List<Cart> getCartsCheckedByuserId(Integer userId);
}