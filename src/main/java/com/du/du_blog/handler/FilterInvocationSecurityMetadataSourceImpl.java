package com.du.du_blog.handler;

import com.du.du_blog.dao.RoleMapper;
import com.du.du_blog.dao.RoleResourceMapper;
import com.du.du_blog.dto.ResourceRoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

/**
 *获取所请求的资源的可访问角色
 */
@Component
public class FilterInvocationSecurityMetadataSourceImpl implements FilterInvocationSecurityMetadataSource {
    /**
     * 资源角色列表
     */
    private static List<ResourceRoleDTO> resourceRoleList;
    @Autowired
    private RoleMapper roleMapper;


    /**
     * 初始化资源角色列表
     * 方法将会在依赖注入完成后被自动调用
     */
    @PostConstruct
    public void getResourceRole(){
        resourceRoleList=roleMapper.listResourceRoles();
    }

    /**
     * 清空接口角色信息(更改资源角色信息时执行
     */
    public void clearDataSource() {
        resourceRoleList = null;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object o) throws IllegalArgumentException {
        if(resourceRoleList==null){
            this.getResourceRole();
        }
        FilterInvocation fi=(FilterInvocation)o;
        //获取用户请求url
        String requestURI = fi.getRequest().getRequestURI();
        //获取用户请求方法
        String method = fi.getRequest().getMethod();
        System.out.println("==============URI:"+requestURI+"=====================");
        System.out.println("==============URL:"+fi.getRequest().getRequestURL().toString()+"=====================");
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for (ResourceRoleDTO resourceRoleDTO : resourceRoleList) {
            if(antPathMatcher.match(resourceRoleDTO.getUrl(),requestURI)&&resourceRoleDTO.getRequestMethod().equals(method)){
                List<String> roleList = resourceRoleDTO.getRoleList();
                if(roleList.isEmpty()){
                    System.out.println("路径："+requestURI+" 没有角色===================");
//                    return null;
                    return  SecurityConfig.createList("disable");
                }
                return SecurityConfig.createList(roleList.toArray(new String[]{}));
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
