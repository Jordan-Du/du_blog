package com.du.du_blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.du.du_blog.dao.RoleMapper;
import com.du.du_blog.dao.RoleResourceMapper;
import com.du.du_blog.dao.UserRoleMapper;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.dto.RoleDTO;
import com.du.du_blog.dto.UserRoleDTO;
import com.du.du_blog.exception.ServerException;
import com.du.du_blog.handler.FilterInvocationSecurityMetadataSourceImpl;
import com.du.du_blog.pojo.Role;
import com.du.du_blog.pojo.RoleMenu;
import com.du.du_blog.pojo.RoleResource;
import com.du.du_blog.pojo.UserRole;
import com.du.du_blog.service.RoleMenuService;
import com.du.du_blog.service.RoleResourceService;
import com.du.du_blog.service.RoleService;
import com.du.du_blog.utils.BeanCopyUtil;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.RoleVO;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {


    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleResourceMapper roleResourceMapper;
    @Autowired
    private RoleResourceServiceImpl roleResourceService;
    @Autowired
    private RoleMenuServiceImpl roleMenuService;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private FilterInvocationSecurityMetadataSourceImpl filterInvocationSecurityMetadataSource;


    @Override
    public List<UserRoleDTO> listUserRoles() {
        List<Role> roles = roleMapper.selectList(new LambdaQueryWrapper<Role>()
                .select(Role::getId, Role::getRoleName));
        return BeanCopyUtil.copyList(roles, UserRoleDTO.class);
    }

    @Override
    public PageDTO<RoleDTO> listRoles(ConditionVO conditionVO) {
        //????????????
        conditionVO.setCurrent((conditionVO.getCurrent()-1)*conditionVO.getSize());
        //??????????????????
        List<RoleDTO> roleDTOS = roleMapper.listRoles(conditionVO);
        Integer count = roleMapper.selectCount(new LambdaQueryWrapper<Role>()
                .like(null != conditionVO.getKeywords(), Role::getRoleName, conditionVO.getKeywords()));
        return new PageDTO(count,roleDTOS);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateRole(RoleVO roleVO) {
        //??????????????????????????????
        Integer count = roleMapper.selectCount(new LambdaQueryWrapper<Role>()
                .eq(Role::getRoleName, roleVO.getRoleName()));
        //????????????????????????
        if(null==roleVO.getId()&&count>0){
            throw new ServerException("??????????????????");
        }
        //?????????????????????
        Role role = Role.builder()
                .id(roleVO.getId())
                .roleName(roleVO.getRoleName())
                .roleLabel(roleVO.getRoleLabel())
                .isDisable(0)
                .build();
        this.saveOrUpdate(role);
        //?????????????????????
        if(roleVO.getResourceIdList()!=null){
            //???????????????????????????
            roleResourceMapper.delete(new LambdaQueryWrapper<RoleResource>()
            .eq(RoleResource::getRoleId,roleVO.getId()));
            //??????????????????????????????
            List<RoleResource> roleResources = roleVO.getResourceIdList().stream()
                    .map(resourceId -> RoleResource.builder()
                            .roleId(roleVO.getId())
                            .resourceId(resourceId)
                            .build())
                    .collect(Collectors.toList());
            roleResourceService.saveBatch(roleResources);
            //????????????????????????
            filterInvocationSecurityMetadataSource.getResourceRole();
        }
        //???????????????????????????
        if(null!=roleVO.getMenuIdList()){
            roleMenuService.remove(new LambdaQueryWrapper<RoleMenu>()
            .eq(RoleMenu::getRoleId,roleVO.getId()));
            List<RoleMenu> roleMenus = roleVO.getMenuIdList().stream()
                    .map(menuId -> RoleMenu.builder()
                            .roleId(roleVO.getId())
                            .menuId(menuId)
                            .build())
                    .collect(Collectors.toList());
            roleMenuService.saveBatch(roleMenus);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteRoles(List<Integer> roleIdList) {
        if(userRoleMapper.selectCount(new LambdaQueryWrapper<UserRole>()
        .in(UserRole::getRoleId,roleIdList))>0){
            throw new ServerException("??????????????????????????????");
        }
        //????????????
        this.remove(new LambdaQueryWrapper<Role>()
        .in(Role::getId,roleIdList));
        //??????????????????
        roleMenuService.remove(new LambdaQueryWrapper<RoleMenu>()
        .in(RoleMenu::getRoleId,roleIdList));
        //??????????????????
        roleResourceService.remove(new LambdaQueryWrapper<RoleResource>()
        .in(RoleResource::getRoleId,roleIdList));
    }
}
