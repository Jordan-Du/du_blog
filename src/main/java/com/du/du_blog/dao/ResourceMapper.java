package com.du.du_blog.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.du.du_blog.dto.ResourceDTO;
import com.du.du_blog.dto.labelOptionDTO;
import com.du.du_blog.pojo.Resource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceMapper extends BaseMapper<Resource> {
    List<ResourceDTO> listResources();
    List<labelOptionDTO> listResourceOption();
}
