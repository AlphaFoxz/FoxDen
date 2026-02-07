package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.domain.system.vo.RouterVo
import com.github.alphafoxz.foxden.domain.system.vo.SysMenuVo

/**
 * 权限服务
 *
 * @author Lion Li
 */
interface SysPermissionService {

    /**
     * 获取角色数据权限
     *
     * @param userId  用户ID
     * @param roleIds 角色ID
     * @return 权限列表
     */
    fun getRoleCustom(userId: Long, roleIds: Array<Long>): String

    /**
     * 获取角色数据权限
     *
     * @param roleIds 角色ID
     * @return 权限列表
     */
    fun getRoleCustom(roleIds: Array<Long>): String

    /**
     * 获取角色权限
     *
     * @param userId  用户ID
     * @param roleIds 角色ID
     * @return 权限列表
     */
    fun getRolePermission(userId: Long, roleIds: Array<Long>): Set<String>

    /**
     * 获取角色权限
     *
     * @param roleIds 角色ID
     * @return 权限列表
     */
    fun getRolePermission(roleIds: Array<Long>): Set<String>

    /**
     * 获取菜单数据权限
     *
     * @param userId  用户ID
     * @param roleIds 角色ID
     * @return 权限列表
     */
    fun getMenuPermission(userId: Long, roleIds: Array<Long>): Set<String>

    /**
     * 获取菜单数据权限
     *
     * @param roleIds 角色ID
     * @return 权限列表
     */
    fun getMenuPermission(roleIds: Array<Long>): Set<String>

    /**
     * 根据用户ID构建路由列表
     *
     * @param userId 用户ID
     * @return 路由列表
     */
    fun buildMenusByUserId(userId: Long): List<RouterVo>

    /**
     * 根据用户ID构建菜单树
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    fun buildMenuTreeByUserId(userId: Long): List<SysMenuVo>
}
