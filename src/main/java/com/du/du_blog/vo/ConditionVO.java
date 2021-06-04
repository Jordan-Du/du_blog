package com.du.du_blog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 查询条件
 *
 * @author 11921
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "查询条件")
public class ConditionVO {


    /**
     * 分类id
     */
    @ApiModelProperty(name = "categoryId", value = "分类id", dataType = "Integer",example = "1")
    private Integer categoryId;

    /**
     * 标签id
     */
    @ApiModelProperty(name = "tagId", value = "标签id", dataType = "Integer",example = "1")
    private Integer tagId;

    /**
     * 当前页码
     */
    @ApiModelProperty(name = "current", value = "当前页码", required = true, dataType = "Integer",example = "1")
    private Integer current;

    /**
     * 显示数量
     */
    @ApiModelProperty(name = "size", value = "显示数量", required = true, dataType = "Integer",example = "1")
    private Integer size;

    /**
     * 搜索内容
     */
    @ApiModelProperty(name = "keywords", value = "搜索内容", dataType = "String")
    private String keywords;

    /**
     * 是否已删除
     */
    @ApiModelProperty(name = "isDelete", value = "是否删除", dataType = "Integer",example = "1")
    private Integer isDelete;

    /**
     * 是否为草稿
     */
    @ApiModelProperty(name = "isDraft", value = "草稿状态", dataType = "Integer",example = "1")
    private Integer isDraft;

    /**
     * 开始时间
     */
    @ApiModelProperty(name = "startTime", value = "开始时间", dataType = "Date")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(name = "endTime", value = "结束时间", dataType = "Date")
    private Date endTime;

}
