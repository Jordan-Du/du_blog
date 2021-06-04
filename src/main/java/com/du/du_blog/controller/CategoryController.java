package com.du.du_blog.controller;

import com.du.du_blog.constant.StatusConst;
import com.du.du_blog.dto.ArticlePreviewListDTO;
import com.du.du_blog.dto.CategoryDTO;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.exception.ServerException;
import com.du.du_blog.pojo.Category;
import com.du.du_blog.service.ArticleService;
import com.du.du_blog.service.CategoryService;
import com.du.du_blog.vo.CategoryVO;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Api(tags = "分类模块")
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ArticleService articleService;

    @ApiOperation(value = "查看分类列表")
    @GetMapping("/categories")
    public Result<PageDTO<CategoryDTO>> listCategories() {
        PageDTO<List<CategoryDTO>> listPageDTO;
        try {
            listPageDTO = categoryService.listCategoryDTO();
            return new Result(true, StatusConst.OK, "查询成功！",listPageDTO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }
    @ApiOperation(value = "查看后台分类列表")
    @GetMapping("/admin/categories")
    public Result<PageDTO<Category>> listCategoryBackDTO(ConditionVO condition) {
        PageDTO<Category> categoryPageDTO;
        try {
            categoryPageDTO = categoryService.listCategoryBackDTO(condition);
            return new Result(true, StatusConst.OK, "查询成功！",categoryPageDTO);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }


    @ApiOperation(value = "添加或修改分类")
    @PostMapping("/admin/categories")
    public Result saveOrUpdateCategory(@Valid @RequestBody CategoryVO categoryVO) {
        try {
            categoryService.saveOrUpdateCategory(categoryVO);
            return new Result(true, StatusConst.OK, "操作成功！");
        }catch (ServerException se){
            log.error(se.getMessage(), se);
            return new Result(false, StatusConst.ERROR, se.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "删除分类")
    @DeleteMapping("/admin/categories")
    public Result deleteCategories(@RequestBody List<Integer> categoryIdList) {
        try {
            categoryService.deleteCategory(categoryIdList);
            return new Result(true, StatusConst.OK, "操作成功！");
        }catch (ServerException se){
            log.error(se.getMessage(), se);
            return new Result(false, StatusConst.ERROR, "该分类下有文章！");
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "查看分类下对应的文章")
    @GetMapping("/categories/{categoryId}")
    public Result<ArticlePreviewListDTO> listArticlesByCategoryId(@PathVariable("categoryId") Integer categoryId, Integer current) {
        ConditionVO conditionVO = ConditionVO.builder()
                .categoryId(categoryId)
                .current(current)
                .build();
        return new Result<>(true, StatusConst.OK, "查询成功", articleService.listArticlesByCondition(conditionVO));
    }


}
