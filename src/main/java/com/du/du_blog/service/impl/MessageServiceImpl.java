package com.du.du_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.du.du_blog.dao.MessageMapper;
import com.du.du_blog.dto.MessageBackDTO;
import com.du.du_blog.dto.MessageDTO;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.pojo.Message;
import com.du.du_blog.service.MessageService;
import com.du.du_blog.utils.BeanCopyUtil;
import com.du.du_blog.utils.IpUtils;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.MessageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Resource
    private HttpServletRequest request;
    @Autowired
    private MessageMapper messageMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveMessage(MessageVO messageVO) {
        Message message = BeanCopyUtil.copyObject(messageVO, Message.class);
        String ipAddr = IpUtils.getIpAddr(request);
        message.setIpAddress(ipAddr);
        message.setIpSource(IpUtils.getIpSource(ipAddr));
        this.save(message);
    }

    @Override
    public List<MessageDTO> listMessages() {
        List<Message> messages = this.list(null);
        return BeanCopyUtil.copyList(messages, MessageDTO.class);
    }

    @Override
    public PageDTO<MessageBackDTO> listMessageBackDTO(ConditionVO condition) {
        Page page = new Page(condition.getCurrent(), condition.getSize());
        Page pageMessage = messageMapper.selectPage(page, new LambdaQueryWrapper<Message>()
                .select(Message::getId, Message::getNickname, Message::getAvatar, Message::getIpAddress, Message::getIpSource, Message::getMessageContent, Message::getCreateTime)
                .like(null != condition.getKeywords(), Message::getNickname, condition.getKeywords())
                .orderByDesc(Message::getCreateTime));
        return new PageDTO((int) pageMessage.getTotal(), pageMessage.getRecords());
    }


    @Override
    public void deleteMessages(List<Integer> messageIdList) {
        messageMapper.delete(new LambdaQueryWrapper<Message>()
                .in(Message::getId, messageIdList));
    }
}
