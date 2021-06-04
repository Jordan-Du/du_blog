package com.du.du_blog.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * email信息
 */
@Data
@AllArgsConstructor
@Builder
public class EmailDTO implements Serializable {
    /**
     * 邮箱号
     */
    private String email;

    /**
     * 主题
     */
    private String subject;

    /**
     * 内容
     */
    private String content;
}
