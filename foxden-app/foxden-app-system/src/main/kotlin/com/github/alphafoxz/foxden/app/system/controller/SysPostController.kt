package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.hutool.core.util.ObjectUtil
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.excel.utils.ExcelUtil
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysDeptBo
import com.github.alphafoxz.foxden.domain.system.bo.SysPostBo
import com.github.alphafoxz.foxden.domain.system.service.SysDeptService
import com.github.alphafoxz.foxden.domain.system.service.SysPostService
import com.github.alphafoxz.foxden.domain.system.vo.SysPostVo
import jakarta.servlet.http.HttpServletResponse
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
    private val postService: SysPostService,
    private val deptService: SysDeptService
) : BaseController() {

    /**
     * 获取岗位列表
     */
    @SaCheckPermission("system:post:list")
    @GetMapping("/list")
    fun list(post: SysPostBo, pageQuery: PageQuery): TableDataInfo<SysPostVo> {
        return postService.selectPagePostList(post, pageQuery)
    }

    /**
     * 导出岗位列表
     */
    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:post:export")
    @PostMapping("/export")
    fun export(post: SysPostBo, response: HttpServletResponse) {
        val list = postService.selectPostList(post)
        ExcelUtil.exportExcel(list, "岗位数据", SysPostVo::class.java, response)
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
        if (!postService.checkPostNameUnique(post)) {
            return R.fail("新增岗位'" + post.postName + "'失败，岗位名称已存在")
        } else if (!postService.checkPostCodeUnique(post)) {
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
        if (!postService.checkPostNameUnique(post)) {
            return R.fail("修改岗位'" + post.postName + "'失败，岗位名称已存在")
        } else if (!postService.checkPostCodeUnique(post)) {
            return R.fail("修改岗位'" + post.postName + "'失败，岗位编码已存在")
        } else if (SystemConstants.DISABLE == post.status && postService.countUserPostById(post.postId!!) > 0) {
            return R.fail("该岗位下存在已分配用户，不能禁用!")
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

    /**
     * 获取岗位选择框列表
     *
     * @param postIds 岗位ID串
     * @param deptId  部门id
     */
    @SaCheckPermission("system:post:query")
    @GetMapping("/optionselect")
    fun optionselect(
        @RequestParam(required = false) postIds: Array<Long>?,
        @RequestParam(required = false) deptId: Long?
    ): R<List<SysPostVo>> {
        val list = if (ObjectUtil.isNotNull(deptId)) {
            val post = SysPostBo(deptId = deptId)
            postService.selectPostList(post)
        } else if (postIds != null) {
            postService.selectPostByIds(postIds.toList())
        } else {
            emptyList()
        }
        return R.ok(list)
    }

    /**
     * 获取部门树列表
     */
    @SaCheckPermission("system:post:list")
    @GetMapping("/deptTree")
    fun deptTree(dept: SysDeptBo): R<List<com.github.alphafoxz.foxden.common.core.domain.Tree<Long>>> {
        return R.ok(deptService.selectDeptTreeList(dept))
    }
}
