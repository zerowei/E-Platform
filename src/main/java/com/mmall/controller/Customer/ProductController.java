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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
