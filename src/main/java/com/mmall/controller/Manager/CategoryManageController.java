package com.mmall.controller.Manager;

import com.mmall.common.Const;
import com.mmall.common.ReturnResponse;
import com.mmall.common.StatusCode;
import com.mmall.pojo.User;
import com.mmall.service.CategoryService;
import com.mmall.service.UserService;
import com.mmall.utils.CookieUtil;
import com.mmall.utils.JsonUtil;
import com.mmall.utils.RedisUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/add_category.do")
    @ResponseBody
    public ReturnResponse addCategory(HttpServletRequest request, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请先登录");
        }

        if (userService.checkSuperUserRole(user).isSuccess()) {
            return categoryService.addCategory(categoryName, parentId);

        } else {
            return ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("/set_category_name.do")
    @ResponseBody
    public ReturnResponse setCategoryName(HttpServletRequest request, Integer categoryId, String categoryName) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请登录");
        }
        if (userService.checkSuperUserRole(user).isSuccess()) {
            //更新categoryName
            return categoryService.updateCategoryName(categoryId, categoryName);
        } else {
            return ReturnResponse.ReturnSuccessByMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("get_categories.do")
    @ResponseBody
    public ReturnResponse getChildrenParallelCategory(HttpServletRequest request, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请登录");
        }
        if (userService.checkSuperUserRole(user).isSuccess()) {
            //查询子节点的category信息,并且不递归,保持平级
            return categoryService.getChildrenParallelCategory(categoryId);
        } else {
            return ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限");
        }
    }

    @RequestMapping("get_all_categories.do")
    @ResponseBody
    public ReturnResponse getCategoryAndDeepChildrenCategory(HttpServletRequest request, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        String cookie = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(cookie)) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "请先登陆再查看个人信息");
        }
        String userInfo = RedisUtil.get(cookie);
        User user = JsonUtil.string2Obj(userInfo, User.class);
        if (user == null) {
            return ReturnResponse.ReturnError(StatusCode.LOGIN_REQUIRE.getCode(), "用户未登录,请登录");
        }
        if (userService.checkSuperUserRole(user).isSuccess()) {
            return categoryService.selectCategoryAndChildrenById(categoryId);
        } else {
            return ReturnResponse.ReturnErrorByMessage("无权限操作,需要管理员权限");
        }
    }
}

