package com.mmall.service;

import com.mmall.common.ReturnResponse;
import com.mmall.pojo.Category;

import java.util.List;

public interface CategoryService {
    ReturnResponse addCategory(String categoryName, Integer parentId);
    ReturnResponse updateCategoryName(Integer categoryId,String categoryName);
    ReturnResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
    ReturnResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
