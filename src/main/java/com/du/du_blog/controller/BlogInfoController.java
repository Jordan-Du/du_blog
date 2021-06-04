package com.du.du_blog.controller;

import com.du.du_blog.constant.StatusConst;
import com.du.du_blog.dto.BlogHomeInfoDTO;
import com.du.du_blog.service.BlogInfoService;
import com.du.du_blog.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Api(tags = "博客信息模块")
@RestController
public class BlogInfoController {
    @Autowired
    private BlogInfoService blogInfoService;

    @ApiOperation(value = "查看博客信息")
    @GetMapping("/")
    public Result<BlogHomeInfoDTO> getBlogHomeInfo() {
        return new Result<>(true, StatusConst.OK, "查询成功", blogInfoService.getBlogInfo());
    }

    @ApiOperation(value = "查看后台信息")
    @GetMapping("/admin")
    public Result<BlogHomeInfoDTO> getBlogBackInfo() {
        return new Result<>(true, StatusConst.OK, "查询成功", blogInfoService.getBlogBackInfo());
    }

    @ApiOperation(value = "查看关于我信息")
    @GetMapping("/about")
    public Result<String> getAbout() {
        return new Result(true, StatusConst.OK, "查询成功", blogInfoService.getAbout());
    }

    @ApiOperation(value = "修改关于我信息")
    @PutMapping("/admin/about")
    public Result updateAbout(String aboutContent) {
        blogInfoService.saveOrUpdateAbout(aboutContent);
        return new Result<>(true, StatusConst.OK, "修改成功");
    }

    @ApiOperation(value = "修改公告")
    @PutMapping("/admin/notice")
    public Result updateNotice(String notice) {
        blogInfoService.saveOrUpdateNotice(notice);
        return new Result<>(true, StatusConst.OK, "修改成功");
    }

//    @ApiOperation(value = "上传语音")
//    @ApiImplicitParam(name = "file", value = "语音文件", required = true, dataType = "MultipartFile")
//    @PostMapping("/voice")
//    public Result<String> saveVoice(VoiceVO voiceVO) throws IOException {
//        webSocketService.sendVoice(voiceVO);
//        return new Result<>(true, StatusConst.OK, "上传成功");
//    }

    @ApiOperation(value = "查看公告")
    @GetMapping("/admin/notice")
    public Result<String> getNotice() {
        return new Result(true, StatusConst.OK, "查看成功", blogInfoService.getNotice());
    }

}
