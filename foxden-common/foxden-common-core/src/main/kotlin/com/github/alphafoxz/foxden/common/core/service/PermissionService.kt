package com.github.alphafoxz.foxden.common.core.service

/**
 * 权限服务接口
 * 用于获取用户的菜单权限和角色权限
 */
interface PermissionService {
    /**
     * 获取用户菜单权限列表
     *
     * @param userId 用户ID
     * @return 菜单权限标识集合
     */
    fun getMenuPermission(userId: Long): Set<String>

    /**
     * 获取用户角色权限列表
     *
     * @param userId 用户ID
     * @return 角色权限标识集合
     */
    fun getRolePermission(userId: Long): Set<String>
}
