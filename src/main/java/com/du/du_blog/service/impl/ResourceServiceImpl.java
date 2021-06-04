package com.du.du_blog.service.impl;

import ch.qos.logback.core.boolex.EvaluationException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.du.du_blog.dao.ResourceMapper;
import com.du.du_blog.dao.RoleResourceMapper;
import com.du.du_blog.dto.ResourceDTO;
import com.du.du_blog.dto.labelOptionDTO;
import com.du.du_blog.exception.ServerException;
import com.du.du_blog.handler.FilterInvocationSecurityMetadataSourceImpl;
import com.du.du_blog.pojo.Resource;
import com.du.du_blog.pojo.RoleResource;
import com.du.du_blog.service.ResourceService;
import com.du.du_blog.service.RoleResourceService;
import com.du.du_blog.service.RoleService;
import com.du.du_blog.utils.BeanCopyUtil;
import com.du.du_blog.vo.ResourceVO;
import jdk.management.resource.ResourceContext;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RoleResourceServiceImpl roleResourceService;
    @Autowired
    private RoleResourceMapper roleResourceMapper;
    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private FilterInvocationSecurityMetadataSourceImpl filterInvocationSecurityMetadataSource;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importSwagger() {
        //删除所有资源信息
        this.remove(null);
        roleResourceService.remove(null);
        Map<String,Object> data = restTemplate.getForObject("http://localhost:8081/v2/api-docs?group=du", Map.class);
        //获取并添加所有模块
        List<Map<String, String>> tags = (List<Map<String, String>>) data.get("tags");
        List<Resource> resources = tags.stream()
                .map(tag -> Resource.builder()
                        .resourceName(tag.get("name"))
                        .isDisable(0)
                        .isAnonymous(0)
                        .build())
                .collect(Collectors.toList());
        this.saveBatch(resources);
        Map<String, Integer> parentResources = resources.stream()
                .collect(Collectors.toMap(Resource::getResourceName, Resource::getId));
        resources.clear();
        //获取并添加所有模块下的资源，并绑定模块和资源
        Map<String, Map<String, Map<String, Object>>> paths = (Map<String, Map<String, Map<String, Object>>>) data.get("paths");
        paths.forEach((url,value)->value.forEach((method,info)->{
            //资源名
            String summary =(String) info.get("summary");
            List<String> tag = (List<String>) info.get("tags");
            Integer parentID = parentResources.get(tag.get(0));
            Resource resource = Resource.builder()
                    .resourceName(summary)
                    //使用正则表达式匹配" {xxx} "并替换成“ * ”
                    .url(url.replaceAll("\\{[^}]*\\}","*"))
                    .requestMethod(method.toUpperCase())
                    .parentId(parentID)
                    .isAnonymous(0)
                    .isDisable(0)
                    .build();
            resources.add(resource);
        }));
        this.saveBatch(resources);
        filterInvocationSecurityMetadataSource.getResourceRole();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateResource(ResourceVO resourceVO) {
        Resource resource = BeanCopyUtil.copyObject(resourceVO, Resource.class);
        this.saveOrUpdate(resource);
        filterInvocationSecurityMetadataSource.getResourceRole();
    }

    @Override
    public void deleteResources(List<Integer> resourceIdList) {
        // 查询是否有角色关联
        Integer count = roleResourceMapper.selectCount(new LambdaQueryWrapper<RoleResource>()
                .in(RoleResource::getResourceId, resourceIdList));
        if (count > 0) {
            throw new ServerException("该资源下存在角色");
        }
        this.removeByIds(resourceIdList);
        filterInvocationSecurityMetadataSource.getResourceRole();
    }

    @Override
    public List<ResourceDTO> listResources() {
        List<ResourceDTO> resourceDTOS = resourceMapper.listResources();
        return resourceDTOS;
    }

    @Override
    public List<labelOptionDTO> listResourceOption() {
        return resourceMapper.listResourceOption();
    }
}
