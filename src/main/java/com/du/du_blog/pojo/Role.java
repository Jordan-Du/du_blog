package com.du.du_blog.pojo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Builder
@Data
@TableName("role")
public class Role {
    /**
     * 角色id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 角色名
     */
    private String roleName;

    /**
     * 角色标签
     */
    private String roleLabel;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT )
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 是否禁用
     */
    private Integer isDisable;

    public Role(String roleName, String roleLabel) {
        this.roleName = roleName;
        this.roleLabel = roleLabel;
    }

    public Role() {
    }
}
