package com.du.du_blog.service;

import com.du.du_blog.dto.UniqueViewDTO;

import java.util.List;

public interface UniqueViewService {
    /**
     * 统计每日用户量
     */
    void saveUniqueView();

    /**
     * 获取7天用户量统计
     * @return 用户量
     */
    List<UniqueViewDTO> listUniqueViews();
}
