package com.mmall.dao;

import com.mmall.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> getProductList();

    List<Product> searchProducts(@Param("productId") Integer productId, @Param("searchName") String searchName);

    List<Product> searchProducts4User(@Param("keyword") String keyword, @Param("categoryIdList") List<Integer> categoryIdList);
}