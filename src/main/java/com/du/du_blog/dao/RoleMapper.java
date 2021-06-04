package com.du.du_blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.du.du_blog.dto.ResourceRoleDTO;
import com.du.du_blog.dto.RoleDTO;
import com.du.du_blog.pojo.Role;
import com.du.du_blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 获取资源角色列表
     * @return
     */
    List<ResourceRoleDTO> listResourceRoles();
    /**
     * 根据用户信息ID查询角色列表
     * @param userInfoId
     * @return
     */
    List<String> listRolesByUserInfoId(@Param("userInfoId") Integer userInfoId);

    /**
     * 查询角色列表
     * @param conditionVO
     * @return
     */
    List<RoleDTO> listRoles(@Param("condition") ConditionVO conditionVO);

}
