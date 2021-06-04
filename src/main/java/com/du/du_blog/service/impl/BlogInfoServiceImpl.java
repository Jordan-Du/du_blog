package com.du.du_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.du.du_blog.constant.RedisPreFixConst;
import com.du.du_blog.dao.*;
import com.du.du_blog.dto.*;
import com.du.du_blog.pojo.Article;
import com.du.du_blog.pojo.UserInfo;
import com.du.du_blog.service.BlogInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.du.du_blog.constant.RedisPreFixConst.ABOUT;
import static com.du.du_blog.constant.RedisPreFixConst.NOTICE;


@Service
public class BlogInfoServiceImpl implements BlogInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UniqueViewServiceImpl uniqueViewService;


    public BlogHomeInfoDTO getBlogInfo() {
        //博主信息
        UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>()
                .select(UserInfo::getAvatar, UserInfo::getNickname, UserInfo::getIntro)
                .eq(UserInfo::getId, 1));
        //文章数量
        Integer articleCount = articleMapper.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDraft, 0)
                .eq(Article::getIsDelete, 0));
        //分类数量
        Integer categoryCount = categoryMapper.selectCount(null);
        //标签数量
        Integer tagCount = tagMapper.selectCount(null);
        //查询公告
        Object o = redisTemplate.boundValueOps(NOTICE).get();
        String notice = null != o ? o.toString() : "暂时没有公告";
        //用户浏览量
        String viewCount = redisTemplate.boundValueOps(RedisPreFixConst.BLOG_VIEWS_COUNT).get().toString();
        return BlogHomeInfoDTO.builder()
                .nickname(userInfo.getNickname())
                .avatar(userInfo.getAvatar())
                .intro(userInfo.getIntro())
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagCount(tagCount)
                .notice(notice)
                .viewsCount(viewCount)
                .build();
    }


    public BlogBackInfoDTO getBlogBackInfo(){
        //访问量
        Integer viewCount = (Integer) redisTemplate.boundValueOps(RedisPreFixConst.BLOG_VIEWS_COUNT).get();
        //留言量
        Integer messageCount = messageMapper.selectCount(null);
        //用户量
        Integer userCount = userInfoMapper.selectCount(null);
        //文章量
        Integer articleCount = articleMapper.selectCount(new LambdaQueryWrapper<Article>()
                .eq(Article::getIsDraft, 0)
                .eq(Article::getIsDelete, 0));
        //查询分类数据
        List<CategoryDTO> categoryDTOS = categoryMapper.listCategoryDTO();
        //一周用户量
        List<UniqueViewDTO> uniqueViewDTOS = uniqueViewService.listUniqueViews();
        //文章浏览量前5
        Map<String,Integer> entries = redisTemplate.boundHashOps(RedisPreFixConst.ARTICLE_VIEWS_COUNT).entries();
        List<Integer> articleIds = Objects.requireNonNull(entries).entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(item -> Integer.valueOf(item.getKey()))
                .collect(Collectors.toList());
        int index=Math.min(articleIds.size(),5);
        articleIds = articleIds.subList(0, index);
        //若无文章，封装数据后返回
        if(articleIds.isEmpty()){
            return BlogBackInfoDTO.builder()
                    .viewsCount(viewCount)
                    .messageCount(messageCount)
                    .userCount(userCount)
                    .articleCount(articleCount)
                    .categoryDTOList(categoryDTOS)
                    .uniqueViewDTOList(uniqueViewDTOS)
                    .build();
        }
        List<Article> articles = articleMapper.listArticleRank(articleIds);
        List<ArticleRankDTO> articleRankDTOS = articles.stream()
                .map(item -> ArticleRankDTO.builder()
                        .articleTitle(item.getArticleTitle())
                        .viewsCount(entries.get(item.getId().toString()))
                        .build())
                .collect(Collectors.toList());
        //封装数据后返回
        return BlogBackInfoDTO.builder()
                .viewsCount(viewCount)
                .messageCount(messageCount)
                .userCount(userCount)
                .articleCount(articleCount)
                .categoryDTOList(categoryDTOS)
                .uniqueViewDTOList(uniqueViewDTOS)
                .articleRankDTOList(articleRankDTOS)
                .build();
    }

    @Override
    public String getAbout() {
        Object value = redisTemplate.boundValueOps(ABOUT).get();
        return Objects.nonNull(value) ? value.toString() : "";
    }

    @Override
    public void saveOrUpdateAbout(String aboutContent) {
        redisTemplate.boundValueOps(ABOUT).set(aboutContent);
    }

    @Override
    public void saveOrUpdateNotice(String notice) {
        redisTemplate.boundValueOps(NOTICE).set(notice);
    }

    @Override
    public String getNotice() {
        Object value = redisTemplate.boundValueOps(NOTICE).get();
        return Objects.nonNull(value) ? value.toString() : "暂时没有公告";
    }

}
