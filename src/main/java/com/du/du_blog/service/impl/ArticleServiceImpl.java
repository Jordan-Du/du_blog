package com.du.du_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.du.du_blog.constant.RedisPreFixConst;
import com.du.du_blog.dao.ArticleMapper;
import com.du.du_blog.dao.ArticleTagMapper;
import com.du.du_blog.dao.CategoryMapper;
import com.du.du_blog.dao.TagMapper;
import com.du.du_blog.dto.*;
import com.du.du_blog.pojo.Article;
import com.du.du_blog.pojo.ArticleTag;
import com.du.du_blog.pojo.Category;
import com.du.du_blog.pojo.Tag;
import com.du.du_blog.service.ArticleService;
import com.du.du_blog.service.ArticleTagService;
import com.du.du_blog.utils.BeanCopyUtil;
import com.du.du_blog.utils.UserUtils;
import com.du.du_blog.vo.ArticleVO;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.DeleteVO;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

import static com.du.du_blog.constant.CommonConst.FALSE;
import static com.du.du_blog.constant.RedisPreFixConst.*;

@EnableAsync
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private ArticleTagService articleTagService;
    @Autowired
    private HttpSession session;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<ArticleHomeDTO> listHomeArticles(Long current) {
        return articleMapper.listArticles((current - 1) * 10);
    }

    @Override
    public PageDTO<ArchiveDTO> listArchives(Long current) {
        Page<Article> page = new Page<>(current,10);
        Page<Article> articleIPage = articleMapper.selectPage(page, new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleTitle, Article::getCreateTime)
                .orderByDesc(Article::getCreateTime)
                .eq(Article::getIsDraft, 0)
                .eq(Article::getIsDraft, 0));
        List<ArchiveDTO> archiveDTOS = BeanCopyUtil.copyList(articleIPage.getRecords(), ArchiveDTO.class);
        return new PageDTO((int)articleIPage.getTotal(),archiveDTOS);
    }

    @Override
    public PageDTO<ArticleBackDTO> listBackArticles(ConditionVO condition) {
        //转换页码
        condition.setCurrent((condition.getCurrent()-1)*condition.getSize());
        //查询文章总数
        Integer integer = articleMapper.countArticles(condition);
        if(integer==0){
            return new PageDTO<>();
        }

        /*封装点赞和浏览量数据*/
        Map<String,Integer> likeCountMap = redisTemplate.boundHashOps(ARTICLE_LIKE_COUNT).entries();
        Map<String,Integer> viewCountMap = redisTemplate.boundHashOps(ARTICLE_VIEWS_COUNT).entries();
        List<ArticleBackDTO> articleBackDTOS = articleMapper.listArticlesBack(condition);
        articleBackDTOS.forEach(item->{
            item.setLikeCount(likeCountMap.get(item.getId().toString()));
            item.setViewsCount(viewCountMap.get(item.getId().toString()));
        });
        return new PageDTO(integer,articleBackDTOS);
    }

    @Override
    public ArticleOptionDTO listArticleOption() {
        List<Tag> tags = tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                .select(Tag::getId, Tag::getTagName));
        List<TagDTO> tagDTOS = BeanCopyUtil.copyList(tags, TagDTO.class);
        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .select(Category::getId, Category::getCategoryName));
        List<CategoryBackDTO> categoryBackDTOS = BeanCopyUtil.copyList(categories, CategoryBackDTO.class);
        return ArticleOptionDTO.builder()
                .categoryDTOList(categoryBackDTOS)
                .tagDTOList(tagDTOS)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateArticles(ArticleVO articleVO) {
        Article article = Article.builder()
                .id(articleVO.getId())
                .userId(UserUtils.getLoginUser().getUserInfoId())
                .categoryId(articleVO.getCategoryId())
                .articleCover(articleVO.getArticleCover())
                .articleTitle(articleVO.getArticleTitle())
                .articleContent(articleVO.getArticleContent())
                .isTop(articleVO.getIsTop())
                .isDraft(articleVO.getIsDraft())
                .build();
        this.saveOrUpdate(article);
        //修改文章，1、删除标签；2、添加标签
        if(null!=article.getId()&&article.getIsDraft().equals(0)){
            articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId,article.getId()));
        }
        // 添加文章标签
        if (!articleVO.getTagIdList().isEmpty()) {
            List<ArticleTag> articleTagList = articleVO.getTagIdList().stream().map(tagId -> ArticleTag.builder()
                    .articleId(article.getId())
                    .tagId(tagId)
                    .build())
                    .collect(Collectors.toList());
            articleTagService.saveBatch(articleTagList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateArticleTop(Integer articleId, Integer isTop) {
        Article article = Article.builder()
                .id(articleId)
                .isTop(isTop)
                .build();
        articleMapper.updateById(article);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateArticleDelete(DeleteVO deleteVO) {
        List<Article> articles = deleteVO.getIdList().stream().map(id -> Article.builder()
                .id(id)
                .isTop(FALSE)
                .isDelete(deleteVO.getIsDelete())
                .build())
                .collect(Collectors.toList());
        this.updateBatchById(articles);
    }

    @Override
    public void deleteArticles(List<Integer> articleIdList) {
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getArticleId,articleIdList));
        articleMapper.deleteBatchIds(articleIdList);
    }

    @Override
    public ArticleVO getArticleBackById(Integer articleId) {
        //查询文章
        Article article = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleTitle, Article::getArticleContent, Article::getArticleCover, Article::getCategoryId, Article::getIsTop, Article::getIsDraft)
                .eq(Article::getId, articleId));
        //查询文章标签
        List<Integer> tagIdList = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>()
                .select(ArticleTag::getTagId)
                .eq(ArticleTag::getArticleId, article.getId()))
                .stream()
                .map(ArticleTag::getTagId).collect(Collectors.toList());

        return ArticleVO.builder()
                .id(article.getId())
                .articleTitle(article.getArticleTitle())
                .articleContent(article.getArticleContent())
                .articleCover(article.getArticleCover())
                .categoryId(article.getCategoryId())
                .isTop(article.getIsTop())
                .tagIdList(tagIdList)
                .isDraft(article.getIsDraft())
                .build();
    }

    @Async
    public void updateArticleViewCount(Integer articleId){
        //用Set来存储访问过的文章ID，并存放在session中，若Set中没有该ID，则表示未访问过
        Set<Integer> set = (Set<Integer>)session.getAttribute("articleSet");
        if(null==set){
            set=new HashSet();
            session.setAttribute("articleSet",set);
        }
        if(!set.contains(articleId)){
            set.add(articleId);
            session.setAttribute("articleSet",set);
            redisTemplate.boundHashOps(RedisPreFixConst.ARTICLE_VIEWS_COUNT).increment(articleId.toString(),1);
        }
    }

    @Override
    public ArticleDTO selectArticleById(Integer articleId) {
        //更新文章浏览量
        updateArticleViewCount(articleId);
        //获取当前文章
        ArticleDTO articleDTO = articleMapper.selectArticleById(articleId);
        //获取前后一篇文章
        Article articleBefore = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleTitle, Article::getArticleCover)
                .eq(Article::getIsDelete, 0)
                .eq(Article::getIsDraft, 0)
                .lt(Article::getId, articleId)
                .last("limit 1"));
        Article articleAfter = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleTitle, Article::getArticleCover)
                .eq(Article::getIsDelete, 0)
                .eq(Article::getIsDraft, 0)
                .gt(Article::getId, articleId)
                .last("limit 1"));
        articleDTO.setLastArticle(BeanCopyUtil.copyObject(articleBefore,ArticlePaginationDTO.class));
        articleDTO.setNextArticle(BeanCopyUtil.copyObject(articleAfter,ArticlePaginationDTO.class));
        //添加推荐文章
        articleDTO.setArticleRecommendList(articleMapper.listArticleRecommends(articleDTO.getId()));
        //封装点赞量和浏览量
        articleDTO.setLikeCount((Integer)redisTemplate.boundHashOps(ARTICLE_LIKE_COUNT).get(articleDTO.getId().toString()));
        articleDTO.setViewsCount((Integer)redisTemplate.boundHashOps(ARTICLE_VIEWS_COUNT).get(articleDTO.getId().toString()));
        return articleDTO;
    }

    @Override
    public void saveArticleLike(Integer articleId) {
        // 查询当前用户点赞过的文章id集合
        Set<Integer> articleLikeSet = (Set<Integer>) redisTemplate.boundHashOps(ARTICLE_USER_LIKE).get(UserUtils.getLoginUser().getUserInfoId().toString());
        // 第一次点赞则创建
        if (CollectionUtils.isEmpty(articleLikeSet)) {
            articleLikeSet = new HashSet<>();
        }
        // 判断是否点赞
        if (articleLikeSet.contains(articleId)) {
            // 点过赞则删除文章id
            articleLikeSet.remove(articleId);
            // 文章点赞量-1
            redisTemplate.boundHashOps(ARTICLE_LIKE_COUNT).increment(articleId.toString(), -1);
        } else {
            // 未点赞则增加文章id
            articleLikeSet.add(articleId);
            // 文章点赞量+1
            redisTemplate.boundHashOps(ARTICLE_LIKE_COUNT).increment(articleId.toString(), 1);
        }
        // 保存点赞记录
        redisTemplate.boundHashOps(ARTICLE_USER_LIKE).put(UserUtils.getLoginUser().getUserInfoId().toString(), articleLikeSet);

    }

    @Override
    public List<ArticleRecommendDTO> listNewestArticles(){
        // 查询最新文章
        List<Article> articleList = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleTitle, Article::getArticleCover, Article::getCreateTime)
                .eq(Article::getIsDelete, FALSE)
                .eq(Article::getIsDraft, FALSE)
                .orderByDesc(Article::getId)
                .last("limit 5"));
        return BeanCopyUtil.copyList(articleList, ArticleRecommendDTO.class);
    }

    @Override
    public ArticlePreviewListDTO listArticlesByCondition(ConditionVO condition) {
        //转换页码
        condition.setCurrent((condition.getCurrent()-1)*9);
        List<ArticlePreviewDTO> articlePreviewDTOS = articleMapper.listArticlesByCondition(condition);
        // 搜索条件对应名(标签或分类名)
        String name;
        if (Objects.nonNull(condition.getCategoryId())) {
            name = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                    .select(Category::getCategoryName)
                    .eq(Category::getId, condition.getCategoryId()))
                    .getCategoryName();
        } else {
            name = tagMapper.selectOne(new LambdaQueryWrapper<Tag>()
                    .select(Tag::getTagName)
                    .eq(Tag::getId, condition.getTagId()))
                    .getTagName();
        }
        return ArticlePreviewListDTO.builder()
                .articlePreviewDTOList(articlePreviewDTOS)
                .name(name)
                .build();
    }


}
