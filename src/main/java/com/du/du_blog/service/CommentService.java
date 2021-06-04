package com.du.du_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.du.du_blog.dto.CommentBackDTO;
import com.du.du_blog.dto.CommentDTO;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.dto.ReplyDTO;
import com.du.du_blog.pojo.Comment;
import com.du.du_blog.vo.CommentVO;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.DeleteVO;

import java.util.List;

public interface CommentService extends IService<Comment> {
    /**
     * 查看评论
     *
     * @param articleId 文章id
     * @param current   当前页码
     * @return CommentListDTO
     */
    PageDTO<CommentDTO> listComments(Integer articleId, Long current);

    /**
     * 查看评论下的回复
     *
     * @param commentId 评论id
     * @param current   当前页码
     * @return 回复列表
     */
    List<ReplyDTO> listRepliesByCommentId(Integer commentId, Long current);

    /**
     * 添加评论
     *
     * @param commentVO 评论对象
     */
    void saveComment(CommentVO commentVO);

    /**
     * 点赞评论
     *
     * @param commentId 评论id
     */
    void saveCommentLike(Integer commentId);

    /**
     * 恢复或删除评论
     *
     * @param deleteVO 逻辑删除对象
     */
    void updateCommentDelete(DeleteVO deleteVO);

    /**
     * 查询后台评论
     *
     * @param condition 条件
     * @return 评论列表
     */
    PageDTO<CommentBackDTO> listCommentBackDTO(ConditionVO condition);

}
