package com.mmall.controller.Manager;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ReturnResponse;
import com.mmall.common.StatusCode;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.FileService;
import com.mmall.service.ProductService;
import com.mmall.service.UserService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.PropertiesUtil;
import com.mmall.utils.RedisShardedUtil;
import com.mmall.vo.ProductVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manager/product")
public class ProductManagerController {

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    FileService fileService;

    @RequestMapping("/addOrUpdateProduct.do")
    @ResponseBody
    public ReturnResponse addOrUpdateProduct(HttpServletRequest request, Product product) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }

        if (userService.checkSuperUserRole(user).isSuccess()) {
            return productService.addOrUpdateProduct(product);
        } else {
            return ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("/updateProductStatus.do")
    @ResponseBody
    public ReturnResponse updateProductStatus(HttpServletRequest request, Integer productId, Integer status) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }

        if (userService.checkSuperUserRole(user).isSuccess()) {
            return productService.updateProductStatus(productId, status);
        } else {
            return ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限");
        }
    }

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

        if (userService.checkSuperUserRole(user).isSuccess()) {
            return productService.getProductDetails(productId);
        } else {
            return ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("/getProductList.do")
    @ResponseBody
    public ReturnResponse<PageInfo> getProductList(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }

        if (userService.checkSuperUserRole(user).isSuccess()) {
            return productService.getProductList(pageNum, pageSize);
        } else {
            return ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("/searchProducts.do")
    @ResponseBody
    public ReturnResponse<PageInfo> searchProducts(HttpServletRequest request, Integer productId, String productName, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }

        if (userService.checkSuperUserRole(user).isSuccess()) {
            return productService.searchProducts(productId, productName, pageNum, pageSize);
        } else {
            return ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("/uploadFile.do")
    @ResponseBody
    public ReturnResponse uploadFile(HttpServletRequest request, @RequestParam(value = "upload_file", required = false) MultipartFile file) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }

        if (userService.checkSuperUserRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String url = PropertiesUtil.getProperties("ftp.server.http.prefix") + fileService.uploadFile(file, path);
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("name", file.getOriginalFilename());
            resultMap.put("url", url);
            return ReturnResponse.ReturnSuccessByData(resultMap);
        } else {
            return ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("/uploadRichText.do")
    @ResponseBody
    public Map uploadRichText(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "upload_file", required = false)MultipartFile file) {
        Map<String, Object> resultMap = new HashMap<>();
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            resultMap.put("success", false);
            resultMap.put("message", "用户未登录");
            return resultMap;
        }
        String userInfo = RedisShardedUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("message", "用户未登录");
            return resultMap;
        }

        if (userService.checkSuperUserRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String url = PropertiesUtil.getProperties("ftp.server.http.prefix") + fileService.uploadFile(file, path);
            if (StringUtils.isBlank(url)) {
                resultMap.put("success", false);
                resultMap.put("message", "上传失败");
                return resultMap;
            }
            resultMap.put("success", true);
            resultMap.put("message", "上传成功");
            resultMap.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return resultMap;
        } else {
            resultMap.put("success", false);
            resultMap.put("message", "用户非管理员，无权限");
            return resultMap;
        }
    }
}
