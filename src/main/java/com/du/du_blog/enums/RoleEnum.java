package com.du.du_blog.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum RoleEnum {
    ADMIN(1,"管理员","admin"),
    USER(2,"用户","user")
    ;
    private final Integer roleId;
    private final String roleName;
    private final String roleLabel;
}
