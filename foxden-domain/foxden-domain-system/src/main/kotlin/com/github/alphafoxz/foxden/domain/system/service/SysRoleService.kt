package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysRoleBo
import com.github.alphafoxz.foxden.domain.system.vo.SysRoleVo

/**
 * 角色业务层
 *
 * @author Lion Li
 */
interface SysRoleService {

    /**
     * 分页查询角色列表
     *
     * @param role      查询条件
     * @param pageQuery 分页参数
     * @return 角色分页列表
     */
    fun selectPageRoleList(role: SysRoleBo, pageQuery: PageQuery): TableDataInfo<SysRoleVo>

    /**
     * 根据条件查询角色数据
     *
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    fun selectRoleList(role: SysRoleBo): List<SysRoleVo>

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    fun selectRolesByUserId(userId: Long): List<SysRoleVo>

    /**
     * 根据用户ID查询角色列表(包含被授权状态)
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    fun selectRolesAuthByUserId(userId: Long): List<SysRoleVo>

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    fun selectRolePermissionByUserId(userId: Long): Set<String>

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    fun selectRoleAll(): List<SysRoleVo>

    /**
     * 根据用户ID获取角色选择框列表
     *
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    fun selectRoleListByUserId(userId: Long): List<Long>

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    fun selectRoleById(roleId: Long): SysRoleVo?

    /**
     * 通过角色ID串查询角色
     *
     * @param roleIds 角色ID串
     * @return 角色列表信息
     */
    fun selectRoleByIds(roleIds: List<Long>): List<SysRoleVo>

    /**
     * 校验角色名称是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    fun checkRoleNameUnique(role: SysRoleBo): Boolean

    /**
     * 校验角色权限是否唯一
     *
     * @param role 角色信息
     * @return 结果
     */
    fun checkRoleKeyUnique(role: SysRoleBo): Boolean

    /**
     * 校验角色是否允许操作
     *
     * @param role 角色信息
     */
    fun checkRoleAllowed(role: SysRoleBo)

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    fun checkRoleDataScope(roleId: Long)

    /**
     * 校验角色是否有数据权限
     *
     * @param roleIds 角色ID列表（支持传单个ID）
     */
    fun checkRoleDataScope(roleIds: List<Long>)

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    fun countUserRoleByRoleId(roleId: Long): Long

    /**
     * 新增保存角色信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    fun insertRole(bo: SysRoleBo): Int

    /**
     * 修改保存角色信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    fun updateRole(bo: SysRoleBo): Int

    /**
     * 修改角色状态
     *
     * @param roleId 角色ID
     * @param status 角色状态
     * @return 结果
     */
    fun updateRoleStatus(roleId: Long, status: String): Int

    /**
     * 修改数据权限信息
     *
     * @param bo 角色信息
     * @return 结果
     */
    fun authDataScope(bo: SysRoleBo): Int

    /**
     * 通过角色ID删除角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    fun deleteRoleById(roleId: Long): Int

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    fun deleteRoleByIds(roleIds: List<Long>): Int

    /**
     * 取消授权用户角色
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 结果
     */
    fun deleteAuthUser(userId: Long, roleId: Long): Int

    /**
     * 批量取消授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    fun deleteAuthUsers(roleId: Long, userIds: Array<Long>): Int

    /**
     * 批量选择授权用户角色
     *
     * @param roleId  角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果
     */
    fun insertAuthUsers(roleId: Long, userIds: Array<Long>): Int

    /**
     * 根据角色ID清除该角色关联的所有在线用户的登录状态（踢出在线用户）
     *
     * <p>
     * 先判断角色是否绑定用户，若无绑定则直接返回
     * 然后遍历当前所有在线Token，查找拥有该角色的用户并强制登出
     * 注意：在线用户量过大时，操作可能导致 Redis 阻塞，需谨慎调用
     * </p>
     *
     * @param roleId 角色ID
     */
    fun cleanOnlineUserByRole(roleId: Long)

    /**
     * 根据用户ID列表清除对应在线用户的登录状态（踢出指定用户）
     *
     * <p>
     * 遍历当前所有在线Token，匹配用户ID列表中的用户，强制登出
     * 注意：在线用户量过大时，操作可能导致 Redis 阻塞，需谨慎调用
     * </p>
     *
     * @param userIds 需要清除的用户ID列表
     */
    fun cleanOnlineUser(userIds: List<Long>)
}
