package com.du.du_blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.du.du_blog.dao.ArticleMapper;
import com.du.du_blog.dao.ArticleTagMapper;
import com.du.du_blog.pojo.Article;
import com.du.du_blog.pojo.ArticleTag;
import com.du.du_blog.service.ArticleTagService;
import org.springframework.stereotype.Service;

@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {
}
