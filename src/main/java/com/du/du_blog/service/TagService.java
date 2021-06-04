package com.du.du_blog.service;

import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.dto.TagDTO;
import com.du.du_blog.pojo.Tag;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.TagVO;

import java.util.List;

public interface TagService {
    /**
     * 查询标签列表
     *
     * @return 标签列表
     */
    PageDTO<TagDTO> listTags();

    /**
     * 查询后台标签
     *
     * @param condition 条件
     * @return 标签列表
     */
    PageDTO<Tag> listTagBackDTO(ConditionVO condition);

    /**
     * 删除标签
     *
     * @param tagIdList 标签id集合
     */
    void deleteTag(List<Integer> tagIdList);

    /**
     * 保存或更新标签
     * @param tagVO 标签
     */
    void saveOrUpdateTag(TagVO tagVO);

}
