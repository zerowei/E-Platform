package com.mmall.controller.Manager;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ReturnResponse;
import com.mmall.pojo.Product;
import com.mmall.service.FileService;
import com.mmall.service.ProductService;
import com.mmall.service.UserService;
import com.mmall.utils.PropertiesUtil;
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
@RequestMapping("/manage/product")
public class ProductManagerController {

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    FileService fileService;

    @RequestMapping("/addOrUpdateProduct.do")
    @ResponseBody
    public ReturnResponse addOrUpdateProduct(Product product) {

        return productService.addOrUpdateProduct(product);
    }

    @RequestMapping("/updateProductStatus.do")
    @ResponseBody
    public ReturnResponse updateProductStatus(Integer productId, Integer status) {

        return productService.updateProductStatus(productId, status);
    }

    @RequestMapping("/getProductDetails.do")
    @ResponseBody
    public ReturnResponse<ProductVO> getProductDetails(Integer productId) {

        return productService.getProductDetails(productId);
    }

    @RequestMapping("/getProductList.do")
    @ResponseBody
    public ReturnResponse<PageInfo> getProductList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        return productService.getProductList(pageNum, pageSize);
    }

    @RequestMapping("/searchProducts.do")
    @ResponseBody
    public ReturnResponse<PageInfo> searchProducts(Integer productId, String productName, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        return productService.searchProducts(productId, productName, pageNum, pageSize);
    }

    @RequestMapping("/uploadFile.do")
    @ResponseBody
    public ReturnResponse uploadFile(HttpServletRequest request, @RequestParam(value = "upload_file", required = false) MultipartFile file) {

        String path = request.getSession().getServletContext().getRealPath("upload");
        String url = PropertiesUtil.getProperties("ftp.server.http.prefix") + fileService.uploadFile(file, path);
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("name", file.getOriginalFilename());
        resultMap.put("url", url);
        return ReturnResponse.ReturnSuccessByData(resultMap);
    }

    @RequestMapping("/uploadRichText.do")
    @ResponseBody
    public Map uploadRichText(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "upload_file", required = false)MultipartFile file) {

        Map<String, Object> resultMap = new HashMap<>();
        String path = request.getSession().getServletContext().getRealPath("upload");
        String uri = fileService.uploadFile(file, path);
        if (StringUtils.isBlank(uri)) {
            resultMap.put("success", false);
            resultMap.put("message", "上传失败");
            return resultMap;
        }
        String url = PropertiesUtil.getProperties("ftp.server.http.prefix") + uri;
        resultMap.put("success", true);
        resultMap.put("message", "上传成功");
        resultMap.put("file_path", url);
        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
        return resultMap;
    }
}
