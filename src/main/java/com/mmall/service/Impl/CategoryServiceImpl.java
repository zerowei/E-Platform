package com.mmall.service.Impl;

import com.mmall.common.ReturnResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("CategoryService")
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public ReturnResponse addCategory(String categoryName, Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ReturnResponse.ReturnErrorByMessage("添加新种类失败");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int count = categoryMapper.insert(category);
        if(count > 0){
            return ReturnResponse.ReturnSuccessByMessage("添加新种类成功");
        }
        return ReturnResponse.ReturnErrorByMessage("添加新种类失败");
    }

    public ReturnResponse updateCategoryName(Integer categoryId, String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ReturnResponse.ReturnErrorByMessage("更新种类参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int count = categoryMapper.updateByPrimaryKeySelective(category);
        if(count > 0){
            return ReturnResponse.ReturnSuccessByMessage("更新种类名字成功");
        }
        return ReturnResponse.ReturnErrorByMessage("更新种类名字失败");
    }

    public ReturnResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            log.info("未找到当前分类的子分类");
        }
        return ReturnResponse.ReturnSuccessByData(categoryList);
    }

    public ReturnResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = new HashSet<>();
        findChildCategory(categorySet,categoryId);


        List<Integer> categoryIdList = new ArrayList<>();
        if(categoryId != null){
            for(Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ReturnResponse.ReturnSuccessByData(categoryIdList);
    }

    private void findChildCategory(Set<Category> categorySet ,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        } else
            return;
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category categoryItem : categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
    }
}
