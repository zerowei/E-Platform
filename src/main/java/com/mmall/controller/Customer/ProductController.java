package com.mmall.controller.Customer;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ReturnResponse;
import com.mmall.common.StatusCode;
import com.mmall.pojo.User;
import com.mmall.service.ProductService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisShardedUtil;
import com.mmall.vo.ProductVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;

    @RequestMapping("/getProductDetails.do")
    @ResponseBody
    public ReturnResponse<ProductVO> getProductDetails(HttpServletRequest request, Integer productId) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
            return productService.getProductDetail4User(productId);
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnResponse<ProductVO> getProductDetailsRestful(HttpServletRequest request, @PathVariable(value = "productId") Integer productId) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        return productService.getProductDetail4User(productId);
    }

    @RequestMapping("/searchProduct.do")
    @ResponseBody
    public ReturnResponse<PageInfo> searchProducts(HttpServletRequest request, @RequestParam(value = "categoryId", required = false) Integer categoryId,
                                                   @RequestParam(value = "keyword", required = false) String keyword,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                                   @RequestParam(value = "order") String order) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        return productService.searchProducts4User(categoryId, keyword, pageNum, pageSize, order);
    }

    @RequestMapping(value = "/{categoryId}/{keyword}/{pageNum}/{pageSize}/{order}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnResponse<PageInfo> searchProductsRestful(HttpServletRequest request, @PathVariable(value = "categoryId") Integer categoryId,
                                                          @PathVariable(value = "keyword") String keyword,
                                                          @PathVariable(value = "pageNum") Integer pageNum,
                                                          @PathVariable(value = "pageSize") Integer pageSize,
                                                          @PathVariable(value = "order") String order) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        if (pageNum == null) pageNum = 1;
        if (pageSize == null) pageSize = 10;
        if (order == null) order = "price_asc";
        return productService.searchProducts4User(categoryId, keyword, pageNum, pageSize, order);
    }

    @RequestMapping(value = "/category/{categoryId}/{pageNum}/{pageSize}/{order}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnResponse<PageInfo> searchProductsRestful(HttpServletRequest request, @PathVariable(value = "categoryId") Integer categoryId,
                                                          @PathVariable(value = "pageNum") Integer pageNum,
                                                          @PathVariable(value = "pageSize") Integer pageSize,
                                                          @PathVariable(value = "order") String order) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        if (pageNum == null) pageNum = 1;
        if (pageSize == null) pageSize = 10;
        if (order == null) order = "price_asc";
        return productService.searchProducts4User(categoryId, null, pageNum, pageSize, order);
    }

    @RequestMapping(value = "/keyword/{keyword}/{pageNum}/{pageSize}/{order}", method = RequestMethod.GET)
    @ResponseBody
    public ReturnResponse<PageInfo> searchProductsRestful(HttpServletRequest request, @PathVariable(value = "keyword") String keyword,
                                                          @PathVariable(value = "pageNum") Integer pageNum,
                                                          @PathVariable(value = "pageSize") Integer pageSize,
                                                          @PathVariable(value = "order") String order) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }
        if (pageNum == null) pageNum = 1;
        if (pageSize == null) pageSize = 10;
        if (order == null) order = "price_asc";
        return productService.searchProducts4User(null, keyword, pageNum, pageSize, order);
    }
}
