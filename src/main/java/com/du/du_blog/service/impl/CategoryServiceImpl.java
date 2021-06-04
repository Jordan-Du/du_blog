package com.du.du_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.du.du_blog.dao.ArticleMapper;
import com.du.du_blog.dao.CategoryMapper;
import com.du.du_blog.dto.CategoryDTO;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.exception.ServerException;
import com.du.du_blog.pojo.Article;
import com.du.du_blog.pojo.Category;
import com.du.du_blog.service.CategoryService;
import com.du.du_blog.vo.CategoryVO;
import com.du.du_blog.vo.ConditionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public PageDTO<List<CategoryDTO>> listCategoryDTO() {
        List<CategoryDTO> categoryDTOS = categoryMapper.listCategoryDTO();
        return new PageDTO(null,categoryDTOS);
    }

    @Override
    public PageDTO<Category> listCategoryBackDTO(ConditionVO conditionVO) {
        Page<Category> page = new Page(conditionVO.getCurrent(), conditionVO.getSize());
        Page<Category> categoryPage = categoryMapper.selectPage(page, new LambdaQueryWrapper<Category>()
                .select()
                .like(StringUtils.isNotBlank(conditionVO.getKeywords()), Category::getCategoryName, conditionVO.getKeywords())
                .orderByDesc(Category::getId));
        return new PageDTO((int)categoryPage.getTotal(),categoryPage.getRecords());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteCategory(List<Integer> categoryIdList) throws ServerException {
        Integer count = articleMapper.selectCount(new LambdaQueryWrapper<Article>()
                .in(Article::getCategoryId, categoryIdList));
        if(count>0){
            throw new ServerException("删除失败，分类下存在文章");
        }
        categoryMapper.deleteBatchIds(categoryIdList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateCategory(CategoryVO categoryVO) {
        Integer count = categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                .eq(Category::getCategoryName, categoryVO.getCategoryName()));
        if(count>0&&categoryVO.getId()==null){
            throw new ServerException("该分类已存在！");
        }
        Category category = Category.builder()
                .id(categoryVO.getId())
                .categoryName(categoryVO.getCategoryName())
                .build();
        this.saveOrUpdate(category);
    }


}
