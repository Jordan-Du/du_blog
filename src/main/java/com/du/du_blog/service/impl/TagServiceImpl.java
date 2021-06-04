package com.du.du_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.du.du_blog.dao.ArticleMapper;
import com.du.du_blog.dao.ArticleTagMapper;
import com.du.du_blog.dao.TagMapper;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.dto.TagDTO;
import com.du.du_blog.exception.ServerException;
import com.du.du_blog.pojo.Article;
import com.du.du_blog.pojo.ArticleTag;
import com.du.du_blog.pojo.Category;
import com.du.du_blog.pojo.Tag;
import com.du.du_blog.service.TagService;
import com.du.du_blog.utils.BeanCopyUtil;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.TagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialException;
import java.util.List;
import java.util.Objects;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleTagMapper articleTagMapper;


    @Override
    public PageDTO<TagDTO> listTags() {
        List<Tag> tags = tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                .select(Tag::getId, Tag::getTagName));
        List<TagDTO> tagDTOS = BeanCopyUtil.copyList(tags, TagDTO.class);
        return new PageDTO(tagMapper.selectCount(null),tagDTOS);
    }

    @Override
    public PageDTO<Tag> listTagBackDTO(ConditionVO condition) {
        Page page = new Page(condition.getCurrent(), condition.getSize());
        Page selectPage = tagMapper.selectPage(page, new LambdaQueryWrapper<Tag>()
                .select()
                .like(Objects.nonNull(condition.getKeywords()), Tag::getTagName, condition.getKeywords())
                .orderByDesc(Tag::getId));
        return new PageDTO((int)page.getTotal(),page.getRecords());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteTag(List<Integer> tagIdList) {
        Integer count = articleTagMapper.selectCount(new LambdaQueryWrapper<ArticleTag>()
                .in(ArticleTag::getTagId, tagIdList));
        if(count>0){
            throw new ServerException("标签下存在文章！！！");
        }
        tagMapper.deleteBatchIds(tagIdList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateTag(TagVO tagVO) {
        Integer count = tagMapper.selectCount(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getTagName, tagVO.getTagName()));
        if (count>0&&tagVO.getId()==null){
            throw new ServerException("该标签已存在！！！！！");
        }
        Tag tag = Tag.builder()
                .id(tagVO.getId())
                .tagName(tagVO.getTagName())
                .build();
        this.saveOrUpdate(tag);
    }
}
