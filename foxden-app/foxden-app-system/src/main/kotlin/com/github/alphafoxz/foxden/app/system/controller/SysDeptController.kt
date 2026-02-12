package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.hutool.core.lang.tree.Tree
import cn.hutool.core.util.StrUtil
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysDeptBo
import com.github.alphafoxz.foxden.domain.system.service.SysDeptService
import com.github.alphafoxz.foxden.domain.system.vo.SysDeptVo
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 部门信息
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/dept")
class SysDeptController(
    private val deptService: SysDeptService
) : BaseController() {

    /**
     * 获取部门列表
     */
    @SaCheckPermission("system:dept:list")
    @GetMapping("/list")
    fun list(dept: SysDeptBo): R<List<SysDeptVo>> {
        return R.ok(deptService.selectDeptList(dept))
    }

    /**
     * 查询部门列表（排除节点）
     *
     * @param deptId 部门ID
     */
    @SaCheckPermission("system:dept:list")
    @GetMapping("/list/exclude/{deptId}")
    fun excludeChild(@PathVariable(required = false) deptId: Long?): R<List<SysDeptVo>> {
        val depts = deptService.selectDeptList(SysDeptBo())
        // 过滤掉指定部门及其子部门
        val filteredDepts = depts.filter { dept ->
            deptId == null || (
                    dept.deptId != deptId &&
                            !StringUtils.splitList(dept.ancestors).contains(deptId.toString())
                    )
        }
        return R.ok(filteredDepts)
    }

    /**
     * 根据部门编号获取详细信息
     */
    @SaCheckPermission("system:dept:query")
    @GetMapping("/{deptId}")
    fun getInfo(@PathVariable deptId: Long): R<SysDeptVo> {
        deptService.checkDeptDataScope(deptId)
        return R.ok(deptService.selectDeptById(deptId))
    }

    /**
     * 获取部门下拉树列表
     */
    @GetMapping("/treeselect")
    fun treeselect(dept: SysDeptBo): R<List<Tree<Long>>> {
        val depts = deptService.selectDeptList(dept)
        return R.ok(deptService.buildDeptTreeSelect(depts))
    }

    /**
     * 新增部门
     */
    @SaCheckPermission("system:dept:add")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated @RequestBody dept: SysDeptBo): R<Void> {
        if (!deptService.checkDeptNameUnique(dept)) {
            return R.fail("新增部门'" + dept.deptName + "'失败，部门名称已存在")
        }
        return toAjax(deptService.insertDept(dept))
    }

    /**
     * 修改部门
     */
    @SaCheckPermission("system:dept:edit")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated @RequestBody dept: SysDeptBo): R<Void> {
        val deptId: Long? = dept.deptId
        deptService.checkDeptDataScope(deptId!!)
        if (!deptService.checkDeptNameUnique(dept)) {
            return R.fail("修改部门'" + dept.deptName + "'失败，部门名称已存在")
        }
        if (dept.parentId == dept.deptId) {
            return R.fail("修改部门'" + dept.deptName + "'失败，上级部门不能是自己")
        }
        if (StrUtil.equals(SystemConstants.DISABLE, dept.status)) {
            if (deptService.selectNormalChildrenDeptById(deptId) > 0) {
                return R.fail("该部门包含未停用的子部门!")
            } else if (deptService.checkDeptExistUser(deptId)) {
                return R.fail("该部门下存在已分配用户，不能禁用!")
            }
        }
        return toAjax(deptService.updateDept(dept))
    }

    /**
     * 删除部门
     */
    @SaCheckPermission("system:dept:remove")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    fun remove(@PathVariable deptId: Long): R<Void> {
        if (SystemConstants.DEFAULT_DEPT_ID == deptId) {
            return R.warn("默认部门,不允许删除")
        }
        if (deptService.hasChildByDeptId(deptId)) {
            return R.warn("存在下级部门,不允许删除")
        }
        if (deptService.checkDeptExistUser(deptId)) {
            return R.warn("部门存在用户,不允许删除")
        }
        deptService.checkDeptDataScope(deptId)
        return toAjax(deptService.deleteDeptById(deptId))
    }
}
