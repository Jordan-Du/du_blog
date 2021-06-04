package com.du.du_blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.du.du_blog.dto.CommentBackDTO;
import com.du.du_blog.dto.CommentDTO;
import com.du.du_blog.dto.ReplyCountDTO;
import com.du.du_blog.dto.ReplyDTO;
import com.du.du_blog.pojo.Comment;
import com.du.du_blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 查看评论
     * @param articleId
     * @param current
     * @return
     */
    List<CommentDTO> listComments(@Param("articleId") Integer articleId, @Param("current") Long current);

    /**
     * 查看评论id集合下的回复
     *
     * @param commentIdList 评论id集合
     * @return 回复集合
     */
    List<ReplyDTO> listReplies(@Param("commentIdList") List<Integer> commentIdList);

    /**
     * 查看当条评论下的回复
     *
     * @param commentId 评论id
     * @param current   当前页码
     * @return 回复集合
     */
    List<ReplyDTO> listRepliesByCommentId(@Param("commentId") Integer commentId, @Param("current") Long current);

    /**
     * 根据评论id查询回复总量
     *
     * @param commentIdList 评论id集合
     * @return 回复数量
     */
    List<ReplyCountDTO> listReplyCountByCommentId(@Param("commentIdList") List<Integer> commentIdList);

    /**
     * 查询后台评论
     *
     * @param condition 条件
     * @return 评论集合
     */
    List<CommentBackDTO> listCommentBackDTO(@Param("condition") ConditionVO condition);

    /**
     * 统计后台评论数量
     *
     * @param condition 条件
     * @return 评论数量
     */
    Integer countCommentDTO(@Param("condition") ConditionVO condition);

}
