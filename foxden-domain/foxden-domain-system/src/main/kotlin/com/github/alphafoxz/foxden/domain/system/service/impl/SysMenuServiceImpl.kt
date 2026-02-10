package com.github.alphafoxz.foxden.domain.system.service.impl

import cn.hutool.core.lang.tree.Tree
import cn.hutool.core.util.StrUtil
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.TreeBuildUtils
import com.github.alphafoxz.foxden.common.jimmer.helper.DataPermissionHelper
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.domain.system.bo.SysMenuBo
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysMenuService
import com.github.alphafoxz.foxden.domain.system.vo.RouterVo
import com.github.alphafoxz.foxden.domain.system.vo.SysMenuVo
import com.github.alphafoxz.foxden.domain.tenant.entity.SysTenantPackage
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

/**
 * Menu 业务层处理
 */
@Service
class SysMenuServiceImpl(
    private val sqlClient: KSqlClient,
    private val jdbcTemplate: org.springframework.jdbc.core.JdbcTemplate
) : SysMenuService {

    override fun selectMenuList(userId: Long): List<SysMenuVo> {
        // 如果是超级管理员，返回所有菜单
        val menus = if (LoginHelper.isSuperAdmin(userId)) {
            sqlClient.createQuery(SysMenu::class) {
                orderBy(table.orderNum.asc())
                select(table)
            }.execute()
        } else {
            // 根据用户角色获取菜单
            selectMenusByUserId(userId)
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
        // 只加载标量字段，不加载关联（避免 UnloadedException）
        // Jimmer 不支持递归 Fetcher，树结构在 buildMenus 中通过 parentId 构建
        val menus = if (LoginHelper.isSuperAdmin(userId)) {
            sqlClient.createQuery(SysMenu::class) {
                orderBy(table.orderNum.asc())
                select(table)
            }.execute()
        } else {
            selectMenusByUserId(userId)
        }

        // 过滤出顶级菜单（parentId == null 或 parentId == 0）
        return menus.filter { menu ->
            val pid = menu.parentId
            pid == null || pid == 0L
        }
    }

    override fun selectMenuListByRoleId(roleId: Long): List<Long> {
        val role = sqlClient.findById(SysRole::class, roleId) ?: return emptyList()
        return role.menus.map { it.id }
    }

    override fun selectMenuListByPackageId(packageId: Long): List<Long> {
        // 查询租户套餐配置的菜单ID
        val tenantPackage = sqlClient.findById(SysTenantPackage::class, packageId)
            ?: return emptyList()

        val menuIdsStr = tenantPackage.menuIds
        if (menuIdsStr.isNullOrBlank()) {
            return emptyList()
        }

        // 解析菜单ID列表（逗号分隔）
        val menuIds = menuIdsStr.split(",")
            .mapNotNull { it.toLongOrNull() }

        if (menuIds.isEmpty()) {
            return emptyList()
        }

        // 如果启用严格模式，需要排除父级菜单ID
        if (tenantPackage.menuCheckStrictly == true) {
            // 查询这些菜单的父级ID
            val parentIds = if (menuIds.isEmpty()) {
                emptyList()
            } else {
                menuIds.mapNotNull { menuId ->
                    sqlClient.findById(SysMenu::class, menuId)?.parentId
                }.filter { it != null && it != 0L }
            }

            // 排除父级菜单，只返回叶子菜单
            return menuIds.filter { it !in parentIds }
        }

        return menuIds
    }

    override fun buildMenus(menus: List<SysMenu>): List<RouterVo> {
        // 获取所有菜单（用于构建子菜单关系）
        val allMenus = sqlClient.createQuery(SysMenu::class) {
            where(table.status eq SystemConstants.NORMAL)
            orderBy(table.orderNum.asc())
            select(table)
        }.execute()

        return buildMenuTree(menus, allMenus)
    }

    /**
     * 递归构建菜单树
     * @param parentMenus 当前层级的父菜单
     * @param allMenus 所有菜单（用于查找子菜单）
     */
    private fun buildMenuTree(parentMenus: List<SysMenu>, allMenus: List<SysMenu>): List<RouterVo> {
        val routers = mutableListOf<RouterVo>()
        for (menu in parentMenus) {
            val name = (menu.path?.capitalize() ?: "") + menu.id
            val router = RouterVo(
                name = name,
                path = getRouterPath(menu),
                hidden = "1" == menu.visible,
                component = getComponentInfo(menu),
                query = parseQuery(menu.queryParam),
                meta = com.github.alphafoxz.foxden.domain.system.vo.MetaVo(
                    title = menu.menuName,
                    icon = menu.icon,
                    noCache = "0" != menu.cacheFlag
                )
            )

            // 查找当前菜单的子菜单
            val cMenus = allMenus.filter { it.parentId == menu.id }
            if (cMenus.isNotEmpty() && SystemConstants.TYPE_DIR == menu.menuType) {
                router.alwaysShow = true
                router.redirect = "noRedirect"
                router.children = buildMenuTree(cMenus, allMenus)
            }

            routers.add(router)
        }
        return routers
    }

    override fun buildMenuTreeSelect(menus: List<SysMenuVo>): List<Tree<Long>> {
        if (menus.isEmpty()) {
            return emptyList()
        }
        return TreeBuildUtils.build(menus) { menu, tree ->
            tree.setId(menu.menuId)
                .setParentId(menu.parentId)
                .setName(menu.menuName)
                .setWeight(menu.orderNum)
            tree.put("menuType", menu.menuType)
            tree.put("icon", menu.icon)
            tree.put("visible", menu.visible)
            tree.put("status", menu.status)
        }
    }

    override fun buildMenuTree(menus: List<SysMenuVo>): List<SysMenuVo> {
        if (menus.isEmpty()) {
            return emptyList()
        }
        // 简单返回原列表，实际树形结构由前端处理
        return menus
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
        // 检查菜单是否被角色关联
        // 简化实现：查询是否有启用的角色存在
        // 注意：完整实现需要直接查询 sys_role_menu 关联表
        // 由于 Jimmer 对 @ManyToMany 反向查询的限制，这里使用简化版本
        try {
            val count = sqlClient.createQuery(SysRole::class) {
                where(table.status eq "0")
                select(table.id)
            }.execute().count()

            // 如果有启用的角色，认为菜单可能被关联
            // TODO: 创建 SysRoleMenu 实体以精确查询关联表
            return count > 0
        } catch (e: Exception) {
            return false
        }
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

    override fun checkRouteConfigUnique(menu: SysMenuBo): Boolean {
        // 按钮类型菜单不需要校验路由
        if (SystemConstants.TYPE_BUTTON == menu.menuType) {
            return true
        }

        val menuId = menu.menuId ?: -1L
        val parentId = menu.parentId ?: 0L
        val path = menu.path ?: return true

        // 查询所有目录和菜单类型的菜单
        val typeDirMenus = sqlClient.createQuery(SysMenu::class) {
            where(table.menuType eq SystemConstants.TYPE_DIR)
            where(table.id ne menuId)
            select(table)
        }.execute()

        val typeMenuMenus = sqlClient.createQuery(SysMenu::class) {
            where(table.menuType eq SystemConstants.TYPE_MENU)
            where(table.id ne menuId)
            select(table)
        }.execute()

        val menus = (typeDirMenus + typeMenuMenus).distinctBy { it.id }

        // 检查是否有重复的路由配置
        for (sysMenu in menus) {
            val dbParentId = sysMenu.parentId ?: 0L
            val dbPath = sysMenu.path ?: ""

            // 同级下不能有相同路由路径
            if (path.equals(dbPath, ignoreCase = true) && parentId == dbParentId) {
                return false
            }

            // 根目录下路由必须唯一
            if (path.equals(dbPath, ignoreCase = true)
                && parentId == 0L
                && dbParentId == 0L) {
                return false
            }
        }

        return true
    }

    /**
     * 获取路由地址
     */
    private fun getRouterPath(menu: SysMenu): String? {
        var routerPath = menu.path
        val parentId = menu.parentId

        // 一级目录（parent_id == 0）且类型为目录（M）且非外链（is_frame=1）
        if ((parentId ?: 0L) == 0L && SystemConstants.TYPE_DIR == menu.menuType && SystemConstants.NO_FRAME == menu.frameFlag) {
            routerPath = "/" + menu.path
        }
        // 一级菜单（parent_id == 0）且类型为菜单（C）且非外链
        else if ((parentId ?: 0L) == 0L && SystemConstants.TYPE_MENU == menu.menuType && SystemConstants.NO_FRAME == menu.frameFlag) {
            routerPath = "/"
        }

        return routerPath
    }

    /**
     * 获取组件信息
     */
    private fun getComponentInfo(menu: SysMenu): String? {
        var component: String? = "Layout"
        if (!StrUtil.isEmpty(menu.component) && !isMenuFrame(menu)) {
            component = menu.component
        }
        return component
    }

    /**
     * 是否为菜单内部跳转（一级菜单）
     */
    private fun isMenuFrame(menu: SysMenu): Boolean {
        return (menu.parentId ?: 0L) == 0L && SystemConstants.TYPE_MENU == menu.menuType && SystemConstants.NO_FRAME == menu.frameFlag
    }

    /**
     * 解析查询参数
     */
    private fun parseQuery(queryParam: String?): Map<String, Any>? {
        if (queryParam.isNullOrBlank()) {
            return null
        }
        // 简单解析 key=value&key2=value2 格式
        return queryParam.split("&")
            .mapNotNull { param ->
                val parts = param.split("=")
                if (parts.size == 2) {
                    parts[0] to parts[1] as Any
                } else {
                    null
                }
            }
            .toMap()
    }

    /**
     * 根据用户ID查询菜单（通过角色）
     */
    private fun selectMenusByUserId(userId: Long): List<SysMenu> {
        // 查询用户及其角色（需要使用 Fetcher 加载 roles 关联）
        val userFetcher = newFetcher(SysUser::class).by {
            allScalarFields()
            roles {
                allScalarFields()
            }
        }

        val user = DataPermissionHelper.ignore(java.util.function.Supplier {
            sqlClient.findById(userFetcher, userId)
        }) ?: return emptyList()

        // 获取角色的菜单ID列表（需要查询每个角色的菜单）
        val roleMenuIds = mutableListOf<Long>()
        for (role in user.roles) {
            // 查询角色的菜单
            val roleWithMenus = sqlClient.findById(
                newFetcher(SysRole::class).by {
                    allScalarFields()
                    menus {
                        allScalarFields()
                    }
                },
                role.id
            )
            roleWithMenus?.menus?.let { menus ->
                roleMenuIds.addAll(menus.map { menu -> menu.id })
            }
        }

        if (roleMenuIds.isEmpty()) {
            return emptyList()
        }

        // 查询菜单详情（只加载标量字段）
        val menus = sqlClient.createQuery(SysMenu::class) {
            where(table.status eq SystemConstants.NORMAL)
            orderBy(table.orderNum.asc())
            select(table)
        }.execute().filter { it.id in roleMenuIds }

        return menus
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
