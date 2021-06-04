package com.du.du_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.du.du_blog.constant.RedisPreFixConst;
import com.du.du_blog.dao.CommentMapper;
import com.du.du_blog.dto.*;
import com.du.du_blog.pojo.Comment;
import com.du.du_blog.service.CommentService;
import com.du.du_blog.utils.UserUtils;
import com.du.du_blog.vo.CommentVO;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.DeleteVO;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageDTO<CommentDTO> listComments(Integer articleId, Long current) {
        //评论数
        Integer count = commentMapper.selectCount(new LambdaQueryWrapper<Comment>()
                .eq(articleId!=null,Comment::getArticleId, articleId)
                .isNull(Comment::getParentId)
                .isNull(articleId==null,Comment::getArticleId)
                .eq(Comment::getIsDelete, 0));
        if(count==0){
            return new PageDTO<>();
        }
        //评论集合
        List<CommentDTO> commentDTOS = commentMapper.listComments(articleId, (current - 1) * 10);
        //评论点赞集合
        Map<String,Integer> likes = (Map<String,Integer>)redisTemplate.boundHashOps(RedisPreFixConst.COMMENT_LIKE_COUNT).entries();
        //评论的回复集合
        List<Integer> commentIdList = commentDTOS.stream()
                .map(item -> item.getId())
                .collect(Collectors.toList());
        List<ReplyDTO> replyDTOS = commentMapper.listReplies(commentIdList);
        Map<Integer, Integer> replyCountMap = commentMapper.listReplyCountByCommentId(commentIdList).stream().collect(Collectors.toMap(ReplyCountDTO::getCommentId, ReplyCountDTO::getReplyCount));
        replyDTOS.forEach(item->item.setLikeCount(likes.get(item.getId().toString())));
        Map<Integer, List<ReplyDTO>> replyGroup = replyDTOS.stream().collect(Collectors.groupingBy(ReplyDTO::getParentId));
        commentDTOS.forEach(item->{
            item.setReplyDTOList(replyGroup.get(item.getId()));
            item.setReplyCount(replyCountMap.get(item.getId()));
            item.setLikeCount(likes.get(item.getId().toString()));
        });
        return new PageDTO<>(count,commentDTOS);
    }

    @Override
    public List<ReplyDTO> listRepliesByCommentId(Integer commentId, Long current) {
        List<ReplyDTO> replyDTOS = commentMapper.listRepliesByCommentId(commentId, (current-1)*5);
        Map<String,Integer> likes = (Map<String,Integer>)redisTemplate.boundHashOps(RedisPreFixConst.COMMENT_LIKE_COUNT).entries();
        replyDTOS.forEach(item->{
            item.setLikeCount(likes.get(item.getId().toString()));
        });
        return replyDTOS;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveComment(CommentVO commentVO) {
        Comment comment = Comment.builder()
                .userId(UserUtils.getLoginUser().getUserInfoId())
                .replyId(commentVO.getReplyId())
                .articleId(commentVO.getArticleId())
                .commentContent(commentVO.getCommentContent())
                .parentId(commentVO.getParentId())
                .build();
        commentMapper.insert(comment);
    }

    @Override
    public void saveCommentLike(Integer commentId) {
        HashSet<Integer> userLikes = (HashSet<Integer>) redisTemplate.boundHashOps(RedisPreFixConst.COMMENT_USER_LIKE).get(UserUtils.getLoginUser().getUserInfoId().toString());
        if(Objects.isNull(userLikes)){
            userLikes = new HashSet<>();
        }
        if(userLikes.contains(commentId)){
            userLikes.remove(commentId);
            redisTemplate.boundHashOps(RedisPreFixConst.COMMENT_LIKE_COUNT).increment(commentId.toString(),-1);
        }else{
            userLikes.add(commentId);
            redisTemplate.boundHashOps(RedisPreFixConst.COMMENT_LIKE_COUNT).increment(commentId.toString(),1);
        }
        redisTemplate.boundHashOps(RedisPreFixConst.COMMENT_USER_LIKE).put(UserUtils.getLoginUser().getUserInfoId().toString(),userLikes);

    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCommentDelete(DeleteVO deleteVO) {
        List<Comment> comments = deleteVO.getIdList().stream()
                .map(item -> Comment.builder()
                        .id(item)
                        .isDelete(deleteVO.getIsDelete())
                        .build())
                .collect(Collectors.toList());
        this.updateBatchById(comments);
    }

    @Override
    public PageDTO<CommentBackDTO> listCommentBackDTO(ConditionVO condition) {
        condition.setCurrent((condition.getCurrent()-1)*condition.getSize());
        Integer countCommentDTO = commentMapper.countCommentDTO(condition);
        List<CommentBackDTO> commentBackDTOS = commentMapper.listCommentBackDTO(condition);
        Map<String,Integer> likes = (Map<String,Integer> )redisTemplate.boundHashOps(RedisPreFixConst.COMMENT_LIKE_COUNT).entries();
        commentBackDTOS.forEach(item->item.setLikeCount(likes.get(item.getId().toString())));
        return new PageDTO<>(countCommentDTO,commentBackDTOS);
    }
}
