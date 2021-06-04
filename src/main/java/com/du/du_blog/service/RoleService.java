package com.du.du_blog.service;

import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.dto.RoleDTO;
import com.du.du_blog.dto.UserRoleDTO;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.RoleVO;

import java.util.List;

public interface RoleService  {

    /**
     * 获取用户角色选项
     *
     * @return 角色
     */
    List<UserRoleDTO> listUserRoles();

    /**
     * 查询角色列表
     *
     * @param conditionVO 条件
     * @return 角色列表
     */
    PageDTO<RoleDTO> listRoles(ConditionVO conditionVO);

    /**
     * 保存或更新角色
     *
     * @param roleVO 角色
     */
    void saveOrUpdateRole(RoleVO roleVO);

    /**
     * 删除角色
     * @param roleIdList 角色id列表
     */
    void deleteRoles(List<Integer> roleIdList);

}
