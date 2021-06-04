package com.du.du_blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.du.du_blog.dto.UserBackDTO;
import com.du.du_blog.pojo.UserAuth;
import com.du.du_blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAuthMapper extends BaseMapper<UserAuth> {
    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    UserAuth selectByUserName(@Param("username") String username);

    /**
     * 查询后台用户列表
     * @param condition
     * @return
     */
    List<UserBackDTO> selectUsers(@Param("condition") ConditionVO condition);

}
