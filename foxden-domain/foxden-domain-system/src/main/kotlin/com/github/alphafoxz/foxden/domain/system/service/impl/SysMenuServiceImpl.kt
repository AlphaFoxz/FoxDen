package com.github.alphafoxz.foxden.domain.system.service.impl

import cn.hutool.core.lang.tree.Tree
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.domain.system.bo.SysMenuBo
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysMenuService
import com.github.alphafoxz.foxden.domain.system.service.SysRoleService
import com.github.alphafoxz.foxden.domain.system.vo.RouterVo
import com.github.alphafoxz.foxden.domain.system.vo.SysMenuVo
import com.github.alphafoxz.foxden.domain.system.vo.SysRoleVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.like
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.springframework.stereotype.Service

/**
 * Menu 业务层处理
 */
@Service
class SysMenuServiceImpl(
    private val sqlClient: KSqlClient,
    private val roleService: SysRoleService
) : SysMenuService {

    override fun selectMenuList(userId: Long): List<SysMenuVo> {
        val user = sqlClient.findById(SysUser::class, userId)
            ?: return emptyList()

        // 手动查询角色数据（避免懒加载问题）
        val roles = roleService.selectRolesByUserId(userId)

        // 如果是管理员，返回所有菜单
        // 否则返回用户角色关联的菜单
        val menus = if (isAdmin(roles)) {
            sqlClient.createQuery(SysMenu::class) {
                orderBy(table.orderNum.asc())
                select(table)
            }.execute()
        } else {
            // TODO: 根据用户角色获取菜单
            sqlClient.createQuery(SysMenu::class) {
                orderBy(table.orderNum.asc())
                select(table)
            }.execute()
        }

        return menus.map { entityToVo(it) }
    }

    override fun selectMenuList(menu: SysMenuBo, userId: Long): List<SysMenuVo> {
        val menus = sqlClient.createQuery(SysMenu::class) {
            menu.menuId?.let { where(table.id eq it) }
            menu.menuName?.takeIf { it.isNotBlank() }?.let { where(table.menuName like "%${it}%") }
            menu.visible?.takeIf { it.isNotBlank() }?.let { where(table.visible eq it) }
            menu.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            menu.menuType?.takeIf { it.isNotBlank() }?.let { where(table.menuType eq it) }
            orderBy(table.orderNum.asc())
            select(table)
        }.execute()

        return menus.map { entityToVo(it) }
    }

    override fun selectMenuPermsByUserId(userId: Long): Set<String> {
        val user = sqlClient.findById(SysUser::class, userId) ?: return emptySet()

        return user.roles
            .flatMap { role ->
                role.menus.mapNotNull { menu -> menu.perms }
            }
            .filter { it.isNotBlank() }
            .toSet()
    }

    override fun selectMenuPermsByRoleId(roleId: Long): Set<String> {
        val role = sqlClient.findById(SysRole::class, roleId) ?: return emptySet()

        return role.menus
            .mapNotNull { menu -> menu.perms }
            .filter { it.isNotBlank() }
            .toSet()
    }

    override fun selectMenuTreeByUserId(userId: Long): List<SysMenu> {
        val user = sqlClient.findById(SysUser::class, userId)
            ?: return emptyList()

        // 手动查询角色数据（避免懒加载问题）
        val roles = roleService.selectRolesByUserId(userId)

        val menus = if (isAdmin(roles)) {
            sqlClient.createQuery(SysMenu::class) {
                orderBy(table.orderNum.asc())
                select(table)
            }.execute()
        } else {
            // TODO: 根据用户角色获取菜单
            sqlClient.createQuery(SysMenu::class) {
                orderBy(table.orderNum.asc())
                select(table)
            }.execute()
        }

        return menus
    }

    override fun selectMenuListByRoleId(roleId: Long): List<Long> {
        val role = sqlClient.findById(SysRole::class, roleId) ?: return emptyList()
        return role.menus.map { it.id }
    }

    override fun selectMenuListByPackageId(packageId: Long): List<Long> {
        // TODO: 实现 selectMenuListByPackageId
        return emptyList()
    }

    override fun buildMenus(menus: List<SysMenu>): List<RouterVo> {
        // TODO: 实现复杂的路由构建逻辑
        // 需要构建树形结构并转换为 RouterVo
        return emptyList()
    }

    override fun buildMenuTreeSelect(menus: List<SysMenuVo>): List<Tree<Long>> {
        // TODO: 实现树形选择器构建逻辑
        return emptyList()
    }

    override fun selectMenuById(menuId: Long): SysMenuVo? {
        val menu = sqlClient.findById(SysMenu::class, menuId) ?: return null
        return entityToVo(menu)
    }

    override fun hasChildByMenuId(menuId: Long): Boolean {
        val count = sqlClient.createQuery(SysMenu::class) {
            where(table.parentId eq menuId)
            select(table.id)
        }.execute().count()
        return count > 0
    }

    override fun hasChildByMenuId(menuIds: List<Long>): Boolean {
        for (menuId in menuIds) {
            if (hasChildByMenuId(menuId)) {
                return true
            }
        }
        return false
    }

    override fun checkMenuExistRole(menuId: Long): Boolean {
        // TODO: 检查菜单是否被角色关联
        // 需要查询 sys_role_menu 表
        return false
    }

    override fun insertMenu(bo: SysMenuBo): Int {
        val newMenu = com.github.alphafoxz.foxden.domain.system.entity.SysMenuDraft.`$`.produce {
            parentId = bo.parentId
            menuName = bo.menuName ?: throw ServiceException("菜单名称不能为空")
            orderNum = bo.orderNum ?: 0
            path = bo.path
            component = bo.component
            queryParam = bo.queryParam
            frameFlag = when (bo.isFrame) {
                "1" -> "1"
                else -> "0"
            }
            cacheFlag = when (bo.isCache) {
                "0" -> "0"
                else -> "1"
            }
            menuType = bo.menuType ?: throw ServiceException("菜单类型不能为空")
            visible = bo.visible ?: "0"
            status = bo.status ?: SystemConstants.NORMAL
            perms = bo.perms
            icon = bo.icon
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(newMenu)
        return if (result.isModified) 1 else 0
    }

    override fun updateMenu(bo: SysMenuBo): Int {
        val menuIdVal = bo.menuId ?: return 0
        val existing = sqlClient.findById(SysMenu::class, menuIdVal)
            ?: throw ServiceException("菜单不存在")

        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysMenuDraft.`$`.produce(existing) {
            bo.parentId?.let { parentId = it }
            bo.menuName?.let { menuName = it }
            bo.orderNum?.let { orderNum = it }
            bo.path?.let { path = it }
            bo.component?.let { component = it }
            bo.queryParam?.let { queryParam = it }
            bo.isFrame?.let {
                frameFlag = when (it) {
                    "1" -> "1"
                    else -> "0"
                }
            }
            bo.isCache?.let {
                cacheFlag = when (it) {
                    "0" -> "0"
                    else -> "1"
                }
            }
            bo.menuType?.let { menuType = it }
            bo.visible?.let { visible = it }
            bo.status?.let { status = it }
            bo.perms?.let { perms = it }
            bo.icon?.let { icon = it }
            updateTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(updated)
        return if (result.isModified) 1 else 0
    }

    override fun deleteMenuById(menuId: Long): Int {
        val result = sqlClient.deleteById(SysMenu::class, menuId)
        return result.totalAffectedRowCount
    }

    override fun deleteMenuById(menuIds: List<Long>) {
        sqlClient.deleteByIds(SysMenu::class, menuIds)
    }

    override fun checkMenuNameUnique(menu: SysMenuBo): Boolean {
        val existing = sqlClient.createQuery(SysMenu::class) {
            where(table.menuName eq menu.menuName)
            where(table.parentId eq (menu.parentId ?: 0L))
            menu.menuId?.let { where(table.id ne it) }
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == menu.menuId
    }

    /**
     * 判断是否是管理员（基于角色列表）
     */
    private fun isAdmin(roles: List<SysRoleVo>): Boolean {
        return roles.any {
            it.roleKey == "admin" || it.roleKey == "role_admin"
        }
    }

    /**
     * 判断是否是管理员（基于用户对象）
     * @deprecated 此方法已废弃，因为 user.roles 是懒加载的，请使用角色列表版本
     */
    private fun isAdmin(user: SysUser): Boolean {
        // 不推荐使用，因为 roles 是懒加载的
        throw UnsupportedOperationException("Use isAdmin(roles: List<SysRoleVo>) instead")
    }

    /**
     * 实体转 VO
     */
    private fun entityToVo(menu: SysMenu): SysMenuVo {
        return SysMenuVo(
            menuId = menu.id,
            parentId = menu.parentId,
            menuName = menu.menuName,
            orderNum = menu.orderNum,
            path = menu.path,
            component = menu.component,
            queryParam = menu.queryParam,
            isFrame = menu.frameFlag,
            isCache = menu.cacheFlag,
            menuType = menu.menuType,
            visible = menu.visible,
            status = menu.status,
            perms = menu.perms,
            icon = menu.icon,
            createTime = menu.createTime
        )
    }
}
