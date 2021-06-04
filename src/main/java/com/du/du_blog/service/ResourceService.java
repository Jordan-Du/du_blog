package com.du.du_blog.service;

import com.du.du_blog.dto.ResourceDTO;
import com.du.du_blog.dto.labelOptionDTO;
import com.du.du_blog.vo.ResourceVO;

import java.util.List;

public interface ResourceService {

    /**
     * 导入swagger权限
     */
    void importSwagger();

    /**
     * 添加或修改资源
     * @param resourceVO 资源对象
     */
    void saveOrUpdateResource(ResourceVO resourceVO);

    /***
     * 删除资源
     * @param resourceIdList 资源id列表
     */
    void deleteResources(List<Integer> resourceIdList);

    /**
     * 查看资源列表
     *
     * @return 资源列表
     */
    List<ResourceDTO> listResources();

    /**
     * 查看资源选项
     * @return 资源选项
     */
    List<labelOptionDTO> listResourceOption();

}
