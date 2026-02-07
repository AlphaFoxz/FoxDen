package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysPostBo
import com.github.alphafoxz.foxden.domain.system.service.SysPostService
import com.github.alphafoxz.foxden.domain.system.vo.SysPostVo
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 岗位信息
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/post")
class SysPostController(
    private val postService: SysPostService
) : BaseController() {

    /**
     * 获取岗位列表
     */
    @SaCheckPermission("system:post:list")
    @GetMapping("/list")
    fun list(post: SysPostBo): R<List<SysPostVo>> {
        return R.ok(postService.selectPostList(post))
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @SaCheckPermission("system:post:query")
    @GetMapping("/{postId}")
    fun getInfo(@PathVariable postId: Long): R<SysPostVo> {
        return R.ok(postService.selectPostById(postId))
    }

    /**
     * 新增岗位
     */
    @SaCheckPermission("system:post:add")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated @RequestBody post: SysPostBo): R<Void> {
        if (!postService.checkPostCodeUnique(post)) {
            return R.fail("新增岗位'" + post.postName + "'失败，岗位编码已存在")
        }
        return toAjax(postService.insertPost(post))
    }

    /**
     * 修改岗位
     */
    @SaCheckPermission("system:post:edit")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated @RequestBody post: SysPostBo): R<Void> {
        if (!postService.checkPostCodeUnique(post)) {
            return R.fail("修改岗位'" + post.postName + "'失败，岗位编码已存在")
        }
        return toAjax(postService.updatePost(post))
    }

    /**
     * 删除岗位
     */
    @SaCheckPermission("system:post:remove")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    fun remove(@PathVariable postIds: Array<Long>): R<Void> {
        postService.deletePostByIds(postIds)
        return R.ok()
    }
}
