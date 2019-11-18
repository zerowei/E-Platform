package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdOrderNum(@Param("userId")Integer userId, @Param("orderNum")Long orderNum);

    Order selectByOrderNum(Long orderNum);

    List<Order> selectByUserId(Integer userId);

    List<Order> selectAll();
}