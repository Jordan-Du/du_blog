package com.du.du_blog.service;

import com.du.du_blog.dto.CategoryDTO;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.exception.ServerException;
import com.du.du_blog.pojo.Category;
import com.du.du_blog.vo.CategoryVO;
import com.du.du_blog.vo.ConditionVO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryService {
    /**
     * 查询文章分类列表
     * @return
     */
    PageDTO<List<CategoryDTO>> listCategoryDTO();

    /**
     * 查询后台分类列表
     * @param conditionVO
     * @return
     */
    PageDTO<Category> listCategoryBackDTO(ConditionVO conditionVO);

    /**
     * 删除分类
     *
     * @param categoryIdList 分类id集合
     */
    void deleteCategory(List<Integer> categoryIdList) throws ServerException;

    /**
     * 添加或修改分类
     * @param categoryVO 分类
     */
    void saveOrUpdateCategory(CategoryVO categoryVO);

}
