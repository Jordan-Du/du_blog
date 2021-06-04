package com.du.du_blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.du.du_blog.dto.UniqueViewDTO;
import com.du.du_blog.pojo.UniqueView;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniqueViewMapper extends BaseMapper<UniqueView> {
    /**
     * 获取七天用户量
     * @param startTime
     * @param endTime
     * @return
     */
    List<UniqueViewDTO> listUniqueViews(@Param("startTime") String startTime, @Param("endTime") String endTime);
}
