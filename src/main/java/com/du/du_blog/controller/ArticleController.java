package com.du.du_blog.controller;

import com.du.du_blog.constant.StatusConst;
import com.du.du_blog.dto.*;
import com.du.du_blog.service.ArticleService;
import com.du.du_blog.vo.ArticleVO;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.DeleteVO;
import com.du.du_blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "文章模块")
@Slf4j
@RestController
public class ArticleController {
    @Autowired
    private ArticleService articleService;


    @ApiOperation(value = "查看文章归档")
    @ApiImplicitParam(name = "current", value = "当前页码", dataType = "Long", example = "1")
    @GetMapping(value = "/articles/{current}/archives")
    public Result<PageDTO<ArchiveDTO>> listArchives(@PathVariable("current") Long current) {
        PageDTO<ArchiveDTO> archiveDTOPageDTO;
        try {
            archiveDTOPageDTO = articleService.listArchives(current);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }

        return new Result(true, StatusConst.OK, "查询成功", archiveDTOPageDTO);
    }

    @ApiOperation(value = "查看文章列表")
    @ApiImplicitParam(name = "current", value = "当前页码", dataType = "Long", example = "1")
    @GetMapping(value = "/{current}/articles")
    public Result<List<ArticleHomeDTO>> listHomeArticles(@PathVariable("current") Long current) {
        List<ArticleHomeDTO> articleHomeDTOS;
        try {
            articleHomeDTOS = articleService.listHomeArticles(current);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
        return new Result<List<ArticleHomeDTO>>(true, StatusConst.OK, "查询成功！", articleHomeDTOS);
    }

    @ApiOperation(value = "后台查看文章")
    @ApiImplicitParam(name = "condition", value = "查询条件", dataType = "ConditionVO")
    @GetMapping(value = "/admin/articles")
    public Result<PageDTO<ArchiveDTO>> listBackArticles(ConditionVO condition) {
        PageDTO<ArticleBackDTO> articleBackDTOPageDTO;
        try {
            articleBackDTOPageDTO = articleService.listBackArticles(condition);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
        return new Result(true, StatusConst.OK, "查询成功", articleBackDTOPageDTO);
    }

    @ApiOperation(value = "获取选项信息")
    @GetMapping("/admin/articles/options")
    public Result<ArticleOptionDTO> listArticleOption() {
        ArticleOptionDTO articleOptionDTO;
        try {
            articleOptionDTO = articleService.listArticleOption();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
        return new Result(true, StatusConst.OK, "查询成功", articleOptionDTO);

    }

    @ApiOperation(value = "添加或修改文章")
    @ApiImplicitParam(name = "articleVO", value = "文章数据", dataType = "ArticleVO")
    @PostMapping("/admin/articles")
    public Result<String> saveOrUpdateArticles(@Valid @RequestBody ArticleVO articleVO) {
        try {
            articleService.saveOrUpdateArticles(articleVO);
            return new Result(true, StatusConst.OK, "操作成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }

    }

    @ApiOperation(value = "修改置顶状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "文章ID", dataType = "Integer", example = "1"),
            @ApiImplicitParam(name = "isTop", value = "置顶状态", dataType = "Integer", example = "1")
    })
    @PostMapping("/admin/articles/top")
    public Result<String> updateArticleTop(@RequestParam("articleId") Integer articleId, @RequestParam("isTop") Integer isTop) {
        try {
            articleService.updateArticleTop(articleId, isTop);
            return new Result(true, StatusConst.OK, "操作成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "逻辑删除或恢复文章")
    @ApiImplicitParam(name = "deleteVO", value = "删除状态数据", dataType = "DeleteVO")
    @PutMapping("/admin/articles")
    public Result<String> updateArticleDelete(DeleteVO deleteVO) {
        try {
            articleService.updateArticleDelete(deleteVO);
            return new Result(true, StatusConst.OK, "操作成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "物理删除文章")
    @ApiImplicitParam(name = "articleIdList", value = "需要删除的文章id集合", dataType = "List<Integer>")
    @DeleteMapping("admin/articles")
    public Result<String> deleteArticles(@RequestBody List<Integer> articleIdList) {
        try {
            articleService.deleteArticles(articleIdList);
            return new Result(true, StatusConst.OK, "操作成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "根据ID查询后台文章")
    @ApiImplicitParam(name = "articleId", value = "文章ID", dataType = "Integer", example = "1")
    @GetMapping("/admin/{articleId}/articles")
    public Result<ArticleVO> getArticleBackById(@PathVariable("articleId") Integer articleId) {
        ArticleVO articleVO;
        try {
            articleVO = articleService.getArticleBackById(articleId);
            return new Result(true, StatusConst.OK, "查询成功！", articleVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "根据ID查询文章")
    @ApiImplicitParam(name = "articleId", value = "文章ID", dataType = "Integer", example = "1")
    @GetMapping("/articles/{articleId}")
    public Result<ArticleDTO> selectArticleById(@PathVariable("articleId") Integer articleId) {
        ArticleDTO articleDTO;
        try {
            articleDTO = articleService.selectArticleById(articleId);
            return new Result(true, StatusConst.OK, "查询成功！", articleDTO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }


/**
  *  ------------------------------------搜索文章--------------------------
*/



    @ApiOperation(value = "点赞文章")
    @ApiImplicitParam(name = "articleId", value = "文章ID", dataType = "Integer", example = "1")
    @PostMapping("/articles/like")
    public Result saveArticleLike(Integer articleId) {
        try {
            articleService.saveArticleLike(articleId);
            return new Result(true, StatusConst.OK, "操作成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "查询最新文章")
    @GetMapping("/articles/newest")
    public Result<List<ArticleRecommendDTO>> listNewestArticles() {
        List<ArticleRecommendDTO> articleRecommendDTOS;
        try {
            articleRecommendDTOS = articleService.listNewestArticles();
            return new Result(true, StatusConst.OK, "查询成功！", articleRecommendDTOS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

}