package com.du.du_blog.service;

import com.du.du_blog.dto.MessageBackDTO;
import com.du.du_blog.dto.MessageDTO;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.MessageVO;

import java.util.List;

public interface MessageService {
    /**
     * 添加留言弹幕
     *
     * @param messageVO 留言对象
     */
    void saveMessage(MessageVO messageVO);

    /**
     * 查看留言弹幕
     *
     * @return 留言列表
     */
    List<MessageDTO> listMessages();

    /**
     * 查看后台留言
     *
     * @param condition 条件
     * @return 留言列表
     */
    PageDTO<MessageBackDTO> listMessageBackDTO(ConditionVO condition);

    /**
     * 删除留言
     * @param messageIdList
     */
    void deleteMessages(List<Integer> messageIdList);
}
