package com.mmall.controller.Manager;

import com.mmall.common.ReturnResponse;
import com.mmall.service.CategoryService;
import com.mmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {
    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/add_category.do")
    @ResponseBody
    public ReturnResponse addCategory(String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId) {

        return categoryService.addCategory(categoryName, parentId);
    }

    @RequestMapping("/set_category_name.do")
    @ResponseBody
    public ReturnResponse setCategoryName(Integer categoryId, String categoryName) {

        return categoryService.updateCategoryName(categoryId, categoryName);
    }

    @RequestMapping("get_categories.do")
    @ResponseBody
    public ReturnResponse getChildrenParallelCategory(@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {

        return categoryService.getChildrenParallelCategory(categoryId);
    }

    @RequestMapping("get_all_categories.do")
    @ResponseBody
    public ReturnResponse getCategoryAndDeepChildrenCategory(@RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {

        return categoryService.selectCategoryAndChildrenById(categoryId);
    }
}

