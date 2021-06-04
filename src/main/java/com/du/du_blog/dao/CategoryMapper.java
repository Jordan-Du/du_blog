package com.du.du_blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.du.du_blog.dto.CategoryDTO;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.pojo.Category;
import org.springframework.stereotype.Repository;
import sun.misc.Cache;

import java.util.List;

@Repository
public interface CategoryMapper extends BaseMapper<Category> {
    /**
     * 查询分类列表
     * @return
     */
    List<CategoryDTO> listCategoryDTO();
}
