package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.domain.system.bo.SysDeptBo
import com.github.alphafoxz.foxden.domain.system.vo.SysDeptVo

/**
 * 部门管理 服务层
 *
 * @author Lion Li
 */
interface SysDeptService {

    /**
     * 查询部门列表
     *
     * @param dept 部门信息
     * @return 部门列表
     */
    fun selectDeptList(dept: SysDeptBo): List<SysDeptVo>

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    fun selectDeptById(deptId: Long): SysDeptVo?

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    fun selectNormalChildrenDeptById(deptId: Long): Int

    /**
     * 是否存在部门子节点
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    fun hasChildByDeptId(deptId: Long): Boolean

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    fun checkDeptExistUser(deptId: Long): Boolean

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    fun checkDeptNameUnique(dept: SysDeptBo): Boolean

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    fun checkDeptDataScope(deptId: Long)

    /**
     * 新增保存部门信息
     *
     * @param bo 部门信息
     * @return 结果
     */
    fun insertDept(bo: SysDeptBo): Int

    /**
     * 修改保存部门信息
     *
     * @param bo 部门信息
     * @return 结果
     */
    fun updateDept(bo: SysDeptBo): Int

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    fun deleteDeptById(deptId: Long): Int

    /**
     * 构建前端所需要下拉树结构
     *
     * @param dept 部门列表
     * @return 下拉树结构列表
     */
    fun buildDeptTreeSelect(depts: List<SysDeptVo>): List<com.github.alphafoxz.foxden.common.core.domain.Tree<Long>>

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    fun selectDeptListByRoleId(roleId: Long): List<Long>

    /**
     * 查询部门树列表
     *
     * @param dept 部门信息
     * @return 部门树列表
     */
    fun selectDeptTreeList(dept: SysDeptBo): List<com.github.alphafoxz.foxden.common.core.domain.Tree<Long>>
}
