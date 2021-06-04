package com.du.du_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.du.du_blog.dto.*;
import com.du.du_blog.pojo.Article;
import com.du.du_blog.vo.ArticleVO;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.DeleteVO;

import java.util.List;

public interface ArticleService extends IService<Article> {
    /**
     * 查询首页文章
     * @param current 当前页码
     * @return
     */
    List<ArticleHomeDTO> listHomeArticles(Long current);
    /**
     * 查询所有文章档案
     * @param current
     * @return
     */
    PageDTO<ArchiveDTO> listArchives(Long current);
    /**
     * 查询后台文章列表
     * @param condition
     * @return
     */
    PageDTO<ArticleBackDTO> listBackArticles(ConditionVO condition);

    /**
     * 查询文章选项（标签和分类）
     * @return
     */
    ArticleOptionDTO listArticleOption();

    /**
     * 添加或修改文章
     * @param articleVO
     */
    void saveOrUpdateArticles(ArticleVO articleVO);

    /**
     * 修改置顶状态
     * @param articleId
     * @param isTop
     */
    void updateArticleTop(Integer articleId,Integer isTop);

    /**
     * 逻辑删除或恢复
     * @param deleteVO
     */
    void updateArticleDelete(DeleteVO deleteVO);

    /**
     * 物理删除文章
     * @param articleIdList
     */
    void deleteArticles(List<Integer> articleIdList);


    /**
     * 通过id查询文章
     * @param articleId
     * @return
     */
    ArticleVO getArticleBackById(Integer articleId);

    /**
     * 更新文章访问量
     * @param articleId
     */
    void updateArticleViewCount(Integer articleId);

    /**
     * 根据文章ID查询文章
     * @param articleId
     * @return
     */
    ArticleDTO selectArticleById(Integer articleId);

    /**
     * 点赞或取消点赞文章
     * @param articleId
     */
    void saveArticleLike(Integer articleId);

    /**
     * 查询最新文章
     * @return
     */
    List<ArticleRecommendDTO> listNewestArticles();

    /**
     * 分类或标签下的文章列表
     * @param condition
     * @return
     */
    ArticlePreviewListDTO listArticlesByCondition(ConditionVO condition);


}
