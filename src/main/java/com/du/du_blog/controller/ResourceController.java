package com.du.du_blog.controller;

import com.du.du_blog.constant.StatusConst;
import com.du.du_blog.dto.ResourceDTO;
import com.du.du_blog.dto.labelOptionDTO;
import com.du.du_blog.service.ResourceService;
import com.du.du_blog.service.impl.ResourceServiceImpl;
import com.du.du_blog.vo.ResourceVO;
import com.du.du_blog.vo.Result;
import io.lettuce.core.api.StatefulConnection;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "资源模块")
@Slf4j
@RestController
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @ApiOperation(value = "导入swagger接口信息")
    @GetMapping("/admin/resources/import/swagger")
    public Result importSwagger() {
        try {
            resourceService.importSwagger();
            return new Result(true, StatusConst.OK,"成功导入资源信息！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusConst.OK,"系统繁忙！");
        }
    }


    @ApiOperation(value = "添加或修改资源")
    @PostMapping("/admin/resources")
    public Result saveOrUpdateResource(@RequestBody  ResourceVO resourceVO){
        try {
            resourceService.saveOrUpdateResource(resourceVO);
            return new Result(true, StatusConst.OK,"添加或修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusConst.OK,"系统繁忙！");
        }
    }


    @ApiOperation(value = "删除资源")
    @DeleteMapping("/admin/resources")
    public Result deleteResources(@RequestBody  List<Integer> resourceIdList){
        try {
            resourceService.deleteResources(resourceIdList);
            return new Result(true, StatusConst.OK,"删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusConst.OK,"系统繁忙！");
        }
    }


    @ApiOperation(value = "查看资源列表")
    @GetMapping("/admin/resources")
    public Result<List<ResourceDTO>> listResources(){
        List<ResourceDTO> resourceDTOS;
        try {
            resourceDTOS = resourceService.listResources();
            return new Result(true, StatusConst.OK,"查询成功！",resourceDTOS);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusConst.OK,"系统繁忙！");
        }
    }


    @ApiOperation(value = "查看资源选项")
    @GetMapping("/admin/role/resources")
    public Result<List<labelOptionDTO>> listResourceOption(){
        List<labelOptionDTO> resourceOption;
        try {
            resourceOption = resourceService.listResourceOption();
            return new Result(true, StatusConst.OK,"查询成功！",resourceOption);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, StatusConst.OK,"系统繁忙！");
        }
    }
}