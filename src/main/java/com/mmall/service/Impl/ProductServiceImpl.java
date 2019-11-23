package com.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ReturnResponse;
import com.mmall.common.StatusCode;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.CategoryService;
import com.mmall.service.ProductService;
import com.mmall.utils.DateUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.vo.ProductListVO;
import com.mmall.vo.ProductVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service("ProductService")
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    CategoryService categoryService;

    private static final String PREFIX = "ftp.server.http.prefix";

    public ReturnResponse addOrUpdateProduct(Product product) {
        if (product != null) {
            if (product.getSubImages() != null) {
                String[] images = product.getSubImages().split(",");
                product.setMainImage(images[0]);
            }
            if (product.getId() != null) {
                int count = productMapper.updateByPrimaryKeySelective(product);
                if (count > 0) return ReturnResponse.ReturnSuccessByMessage("更新商品信息成功");
                return ReturnResponse.ReturnErrorByMessage("更新商品失败");
            } else {
                int count = productMapper.insertSelective(product);
                if (count > 0) return ReturnResponse.ReturnSuccessByMessage("加入新商品成功");
                return ReturnResponse.ReturnErrorByMessage("加入新商品失败");
            }
        }
        return ReturnResponse.ReturnErrorByMessage("传入的产品信息有误");
    }

    public ReturnResponse updateProductStatus(Integer productId, Integer status) {
        if (productId == null || status == null) return ReturnResponse.ReturnError(StatusCode.ILLEGAL_PARAMETERS.getCode(), "参数有误，请检查");
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count > 0) return ReturnResponse.ReturnSuccessByMessage("更新商品状态成功");
        return ReturnResponse.ReturnErrorByMessage("更新商品状态时发生错误");
    }

    public ReturnResponse<ProductVO> getProductDetails(Integer productId) {
        if (productId == null) return ReturnResponse.ReturnError(StatusCode.ILLEGAL_PARAMETERS.getCode(), "参数有误，请检查");
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) return ReturnResponse.ReturnErrorByMessage("传入的ID有误或者商品已下架");
        ProductVO productVO = transfer2VO(product);
        return ReturnResponse.ReturnSuccess("查询商品详细信息成功", productVO);
    }
    
    private ProductVO transfer2VO(Product product) {
        ProductVO productVO = new ProductVO();
        productVO.setId(product.getId());
        productVO.setCategoryId(product.getCategoryId());
        productVO.setName(product.getName());
        productVO.setSubtitle(product.getSubtitle());
        productVO.setMainImage(product.getMainImage());
        productVO.setSubImages(product.getSubImages());
        productVO.setDetail(product.getDetail());
        productVO.setPrice(product.getPrice());
        productVO.setStock(product.getStock());
        productVO.setStatus(product.getStatus());

        productVO.setImageAddressPrefix(PropertiesUtil.getProperties(PREFIX));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        productVO.setParentId(category.getParentId());
        productVO.setCreateTime(DateUtil.transfer2Str(product.getCreateTime()));
        productVO.setUpdateTime(DateUtil.transfer2Str(product.getUpdateTime()));
        return productVO;
    }

    public ReturnResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.getProductList();
        List<ProductListVO> productListVOs = new ArrayList<>();
        for (Product product : products) {
            ProductListVO productListVO = new ProductListVO(product);
            productListVOs.add(productListVO);
        }
        PageInfo res = new PageInfo(products);
        res.setList(productListVOs);
        return ReturnResponse.ReturnSuccess("查询商品列表成功", res);
    }

    public ReturnResponse<PageInfo> searchProducts(Integer productId, String productName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ProductListVO> productList = new ArrayList<>();

        if (!StringUtils.isBlank(productName)) {
            productName = "%" + productName + "%";
        }
        List<Product> res = productMapper.searchProducts(productId, productName);
        for (Product pro : res) {
            ProductListVO productListVO = new ProductListVO(pro);
            productList.add(productListVO);
        }
        PageInfo info = new PageInfo(res);
        info.setList(productList);
        return ReturnResponse.ReturnSuccess("搜索商品列表成功", info);
    }

    public ReturnResponse<ProductVO> getProductDetail4User(Integer productId) {
        if (productId == null) return ReturnResponse.ReturnError(StatusCode.ILLEGAL_PARAMETERS.getCode(), "参数有误，请检查");
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) return ReturnResponse.ReturnErrorByMessage("传入的ID有误");
        if (product.getStatus() == Const.SaleStatus.OFF_SALE.getCode())
            return ReturnResponse.ReturnErrorByMessage("商品已下架");
        ProductVO productVO = transfer2VO(product);
        return ReturnResponse.ReturnSuccess("查询商品详细信息成功", productVO);
    }

    public ReturnResponse<PageInfo> searchProducts4User(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String order) {
        List<Integer> categoryIdList = new ArrayList<>();

        if (categoryId == null && StringUtils.isBlank(keyword))
            return ReturnResponse.ReturnError(StatusCode.ILLEGAL_PARAMETERS.getCode(), StatusCode.ILLEGAL_PARAMETERS.getName());
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                PageHelper.startPage(pageNum, pageSize);
                PageInfo pageInfo = new PageInfo(new ArrayList<ProductListVO>());
                return ReturnResponse.ReturnSuccessByData(pageInfo);
            }
            categoryIdList = categoryService.selectCategoryAndChildrenById(categoryId).getData();
        }

        keyword = (StringUtils.isBlank(keyword)) ? null : "%" + keyword + "%";
        categoryIdList = (categoryIdList.size() == 0) ? null : categoryIdList;

        if (StringUtils.isNotBlank(order) && Const.ORDERSET.contains(order)) {
            String[] temp = order.split("_");
            PageHelper.orderBy(temp[0] + " " + temp[1]);
        }

        List<ProductListVO> productList = new ArrayList<>();
        List<Product> res = productMapper.searchProducts4User(keyword, categoryIdList);
        for (Product pro : res) {
            ProductListVO productListVO = new ProductListVO(pro);
            productList.add(productListVO);
        }
        PageInfo info = new PageInfo(res);
        info.setList(productList);
        return ReturnResponse.ReturnSuccess("搜索商品成功", info);

    }
}
