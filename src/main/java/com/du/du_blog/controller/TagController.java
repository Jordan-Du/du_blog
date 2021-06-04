package com.du.du_blog.controller;

import com.du.du_blog.constant.StatusConst;
import com.du.du_blog.dto.ArticlePreviewListDTO;
import com.du.du_blog.dto.PageDTO;
import com.du.du_blog.dto.TagDTO;
import com.du.du_blog.exception.ServerException;
import com.du.du_blog.pojo.Tag;
import com.du.du_blog.service.ArticleService;
import com.du.du_blog.service.TagService;
import com.du.du_blog.vo.ConditionVO;
import com.du.du_blog.vo.Result;
import com.du.du_blog.vo.TagVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Api(tags = "标签模块")
@RestController
public class TagController {
    @Autowired
    private TagService tagService;
    @Autowired
    private ArticleService articleService;

    @ApiOperation(value = "查看标签列表")
    @GetMapping("/tags")
    public Result<PageDTO<TagDTO>> listTags() {
        PageDTO<TagDTO> tagDTOPageDTO;
        try {
            tagDTOPageDTO = tagService.listTags();
            return new Result(true, StatusConst.OK, "查询成功！", tagDTOPageDTO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "查询后台文章列表")
    @GetMapping("/admin/tags")
    public Result<PageDTO<Tag>> listTagBackDTO(ConditionVO condition) {
        PageDTO<Tag> tagPageDTO;
        try {
            tagPageDTO = tagService.listTagBackDTO(condition);
            return new Result(true, StatusConst.OK, "查询成功！", tagPageDTO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "添加或修改标签")
    @PostMapping("/admin/tags")
    public Result saveOrUpdateTag(@Valid @RequestBody TagVO tagVO) {
        try {
            tagService.saveOrUpdateTag(tagVO);
            return new Result(true, StatusConst.OK, "操作成功！");
        }catch (ServerException se){
            log.error(se.getMessage(),se);
            return new Result(false, StatusConst.ERROR, se.getMessage());
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "删除标签")
    @DeleteMapping("/admin/tags")
    public Result deleteTag(@RequestBody  List<Integer> tagIdList) {
        try {
            tagService.deleteTag(tagIdList);
            return new Result(true, StatusConst.OK, "操作成功！");
        } catch (ServerException se) {
            log.error(se.getMessage(), se);
            return new Result(false, StatusConst.ERROR, se.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new Result(false, StatusConst.ERROR, "系统繁忙！");
        }
    }

    @ApiOperation(value = "查询标签下的文章列表")
    @GetMapping("/tags/{tagId}")
    public Result<ArticlePreviewListDTO> listArticlesByTagId(@PathVariable("tagId") Integer tagId, Integer current) {
        ConditionVO condition = ConditionVO.builder()
                .tagId(tagId)
                .current(current)
                .build();
        return new Result<>(true, StatusConst.OK, "查询成功", articleService.listArticlesByCondition(condition));
    }

}
