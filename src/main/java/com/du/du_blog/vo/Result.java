package com.du.du_blog.vo;

import com.du.du_blog.constant.StatusConst;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "接口返回结果")
public class Result<T> implements Serializable {
    @ApiModelProperty(name="flag",value = "成功标志",dataType = "boolean")
    private boolean flag;
    @ApiModelProperty(name="code",value = "响应代码",dataType = "Integer")
    private Integer code;
    @ApiModelProperty(name="message",value = "消息",dataType = "String")
    private String message;
    @ApiModelProperty(name="data",value = "数据")
    private T data;
    public Result(boolean flag, Integer code, String message, Object data) {
        this.flag = flag;
        this.code = code;
        this.message = message;
        this.data = (T) data;
    }

    public Result(boolean flag, Integer code, String message) {
        this.flag = flag;
        this.code = code;
        this.message = message;
    }

    public Result() {
        this.flag = true;
        this.code = StatusConst.OK;
        this.message = "操作成功!";
    }

}
