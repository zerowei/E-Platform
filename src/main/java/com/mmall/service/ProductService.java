package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ReturnResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductVO;


public interface ProductService {
    ReturnResponse addOrUpdateProduct(Product product);
    ReturnResponse updateProductStatus(Integer productId, Integer status);
    ReturnResponse<ProductVO> getProductDetails(Integer productId);
    ReturnResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);
    ReturnResponse<PageInfo> searchProducts(Integer productId, String productName, Integer pageNum, Integer pageSize);
    ReturnResponse<ProductVO> getProductDetail4User(Integer productId);
    ReturnResponse<PageInfo> searchProducts4User(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String order);
}
