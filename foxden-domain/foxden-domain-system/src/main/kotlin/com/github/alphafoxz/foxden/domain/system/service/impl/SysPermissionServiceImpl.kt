package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.common.core.constant.TenantConstants
import com.github.alphafoxz.foxden.common.core.service.PermissionService
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.domain.system.service.SysMenuService
import com.github.alphafoxz.foxden.domain.system.service.SysPermissionService
import com.github.alphafoxz.foxden.domain.system.service.SysRoleService
import com.github.alphafoxz.foxden.domain.system.vo.RouterVo
import com.github.alphafoxz.foxden.domain.system.vo.SysMenuVo
import org.springframework.stereotype.Service

/**
 * 用户权限处理
 *
 * @author ruoyi
 */
@Service
class SysPermissionServiceImpl(
    private val roleService: SysRoleService,
    private val menuService: SysMenuService
) : SysPermissionService, PermissionService {

    /**
     * 获取角色数据权限
     *
     * @param userId  用户id
     * @return 角色权限信息
     */
    override fun getRolePermission(userId: Long): Set<String> {
        val roles = mutableSetOf<String>()
        // 管理员拥有所有权限
        if (LoginHelper.isSuperAdmin(userId)) {
            roles.add(TenantConstants.SUPER_ADMIN_ROLE_KEY)
        } else {
            roles.addAll(roleService.selectRolePermissionByUserId(userId))
        }
        return roles
    }

    /**
     * 获取菜单数据权限
     *
     * @param userId  用户id
     * @return 菜单权限信息
     */
    override fun getMenuPermission(userId: Long): Set<String> {
        val perms = mutableSetOf<String>()
        // 管理员拥有所有权限
        if (LoginHelper.isSuperAdmin(userId)) {
            perms.add("*:*:*")
        } else {
            perms.addAll(menuService.selectMenuPermsByUserId(userId))
        }
        return perms
    }

    /**
     * 获取角色数据权限
     *
     * @param userId  用户ID
     * @param roleIds 角色ID
     * @return 权限列表
     */
    override fun getRoleCustom(userId: Long, roleIds: Array<Long>): String {
        // TODO: 实现数据权限自定义逻辑
        // 获取角色数据权限配置
        // 1=全部数据权限 2=自定义数据权限 3=本部门数据权限 4=本部门及以下数据权限 5=仅本人数据权限
        return "1"
    }

    /**
     * 获取角色数据权限
     *
     * @param roleIds 角色ID
     * @return 权限列表
     */
    override fun getRoleCustom(roleIds: Array<Long>): String {
        return getRoleCustom(0L, roleIds)
    }

    /**
     * 获取角色权限
     *
     * @param userId  用户ID
     * @param roleIds 角色ID
     * @return 权限列表
     */
    override fun getRolePermission(userId: Long, roleIds: Array<Long>): Set<String> {
        if (LoginHelper.isSuperAdmin(userId)) {
            return setOf(TenantConstants.SUPER_ADMIN_ROLE_KEY)
        }
        return roleService.selectRolePermissionByUserId(userId)
    }

    /**
     * 获取角色权限
     *
     * @param roleIds 角色ID
     * @return 权限列表
     */
    override fun getRolePermission(roleIds: Array<Long>): Set<String> {
        val permissions = mutableSetOf<String>()
        for (roleId in roleIds) {
            permissions.addAll(menuService.selectMenuPermsByRoleId(roleId))
        }
        return permissions
    }

    /**
     * 获取菜单数据权限
     *
     * @param userId  用户ID
     * @param roleIds 角色ID
     * @return 权限列表
     */
    override fun getMenuPermission(userId: Long, roleIds: Array<Long>): Set<String> {
        if (LoginHelper.isSuperAdmin(userId)) {
            return setOf("*:*:*")
        }
        return menuService.selectMenuPermsByUserId(userId)
    }

    /**
     * 获取菜单数据权限
     *
     * @param roleIds 角色ID
     * @return 权限列表
     */
    override fun getMenuPermission(roleIds: Array<Long>): Set<String> {
        val permissions = mutableSetOf<String>()
        for (roleId in roleIds) {
            permissions.addAll(menuService.selectMenuPermsByRoleId(roleId))
        }
        return permissions
    }

    /**
     * 根据用户ID构建路由列表
     *
     * @param userId 用户ID
     * @return 路由列表
     */
    override fun buildMenusByUserId(userId: Long): List<RouterVo> {
        // 获取 SysMenu 实体列表用于构建路由
        val menuEntities = menuService.selectMenuTreeByUserId(userId)
        return menuService.buildMenus(menuEntities)
    }

    /**
     * 根据用户ID构建菜单树
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    override fun buildMenuTreeByUserId(userId: Long): List<SysMenuVo> {
        val menus = menuService.selectMenuList(userId)
        return menuService.buildMenuTree(menus)
    }
}
