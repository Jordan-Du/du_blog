package com.du.du_blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.du.du_blog.dto.*;
import com.du.du_blog.pojo.Article;
import com.du.du_blog.vo.ConditionVO;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 查询首页文章
     *
     * @param current 当前页码
     * @return 首页文章集合
     */
    List<ArticleHomeDTO> listArticles(Long current);

    /**
     * 查询文章数量
     * @param condition
     * @return
     */
    Integer countArticles(@Param("condition") ConditionVO condition);

    /**
     * 查询后台文章
     * @param condition
     * @return
     */
    List<ArticleBackDTO> listArticlesBack(@Param("condition") ConditionVO condition);


    /**
     * 根据文章id查询文章
     * @param articleId
     * @return
     */
    ArticleDTO selectArticleById(Integer articleId);

    /**
     * 查看文章的推荐文章
     * @param articleId 文章id
     * @return 推荐文章
     */
    List<ArticleRecommendDTO> listArticleRecommends(@Param("articleId") Integer articleId);

    /**
     * 根据条件（类型或标签）查询文章列表
     * @param condition
     * @return
     */
    List<ArticlePreviewDTO> listArticlesByCondition(@Param("condition") ConditionVO condition);

    /**
     * 查询文章排行
     * @param articleIdList
     * @return
     */
    List<Article> listArticleRank(@Param("articleIdList") List<Integer> articleIdList);


}
