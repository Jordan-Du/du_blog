package com.du.du_blog.controller;

import com.du.du_blog.constant.StatusConst;
import com.du.du_blog.dto.MessageBackDTO;
import com.du.du_blog.dto.MessageDTO;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.service.MessageService;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.MessageVO;
import com.du.du_blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "留言模块")
@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @ApiOperation(value = "添加留言")
    @PostMapping("/messages")
    public Result saveMessage(@Valid @RequestBody  MessageVO messageVO){
        try {
            messageService.saveMessage(messageVO);
            return new Result(true,StatusConst.OK,"添加成功!");
        } catch (Exception e) {
            return new Result(false, StatusConst.ERROR,"系统繁忙!");
        }
    }

    @ApiOperation(value = "查看留言弹幕")
    @GetMapping("/messages")
    public Result<List<MessageDTO>> listMessages(){
        List<MessageDTO> messageDTOS;
        try {
            messageDTOS = messageService.listMessages();
            return new Result(true,StatusConst.OK,"查询成功!",messageDTOS);
        } catch (Exception e) {
            return new Result(false, StatusConst.ERROR,"系统繁忙!");
        }
    }


    @ApiOperation(value = "查看后台留言")
    @GetMapping("/admin/messages")
    public Result<PageDTO<MessageBackDTO>> listMessageBackDTO(ConditionVO condition){
        PageDTO<MessageBackDTO> messageBackDTOPageDTO = messageService.listMessageBackDTO(condition);
        return new Result(true,StatusConst.OK,"查询成功!",messageBackDTOPageDTO);
    }


    @ApiOperation(value = "删除留言")
    @DeleteMapping("/admin/messages")
    public Result deleteMessages(@RequestBody List<Integer> messageIdList){
        messageService.deleteMessages(messageIdList);
        return new Result(true,StatusConst.OK,"删除成功!");
    }
}
