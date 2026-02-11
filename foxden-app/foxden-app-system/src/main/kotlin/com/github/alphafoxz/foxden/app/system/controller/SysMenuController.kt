package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import cn.hutool.core.lang.tree.Tree
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.constant.TenantConstants
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysMenuBo
import com.github.alphafoxz.foxden.domain.system.service.SysMenuService
import com.github.alphafoxz.foxden.domain.system.service.SysTenantPackageService
import com.github.alphafoxz.foxden.domain.system.service.extensions.deleteMenu
import com.github.alphafoxz.foxden.domain.system.vo.RouterVo
import com.github.alphafoxz.foxden.domain.system.vo.SysMenuVo
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 菜单信息
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/menu")
class SysMenuController(
    private val menuService: SysMenuService,
    private val tenantPackageService: SysTenantPackageService
) : BaseController() {

    /**
     * 获取路由信息
     */
    @GetMapping("/getRouters")
    fun getRouters(): R<List<RouterVo>> {
        val userId = LoginHelper.getUserId() ?: 0L
        val menus = menuService.selectMenuTreeByUserId(userId)
        return R.ok(menuService.buildMenus(menus))
    }

    /**
     * 获取菜单列表
     */
    @SaCheckRole(
        value = [TenantConstants.SUPER_ADMIN_ROLE_KEY, TenantConstants.TENANT_ADMIN_ROLE_KEY],
        mode = SaMode.OR
    )
    @SaCheckPermission("system:menu:list")
    @GetMapping("/list")
    fun list(menu: SysMenuBo): R<List<SysMenuVo>> {
        val userId = LoginHelper.getUserId() ?: 0L
        val menus = menuService.selectMenuList(menu, userId)
        return R.ok(menus)
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @SaCheckRole(
        value = [TenantConstants.SUPER_ADMIN_ROLE_KEY, TenantConstants.TENANT_ADMIN_ROLE_KEY],
        mode = SaMode.OR
    )
    @SaCheckPermission("system:menu:query")
    @GetMapping("/{menuId}")
    fun getInfo(@PathVariable menuId: Long): R<SysMenuVo> {
        return R.ok(menuService.selectMenuById(menuId))
    }

    /**
     * 获取菜单下拉树列表
     */
    @SaCheckPermission("system:menu:query")
    @GetMapping("/treeselect")
    fun treeselect(menu: SysMenuBo): R<List<Tree<Long>>> {
        val userId = LoginHelper.getUserId() ?: 0L
        val menus = menuService.selectMenuList(menu, userId)
        return R.ok(menuService.buildMenuTreeSelect(menus))
    }

    /**
     * 加载对应角色菜单列表树
     */
    @SaCheckPermission("system:menu:query")
    @GetMapping("/roleMenuTreeselect/{roleId}")
    fun roleMenuTreeselect(@PathVariable roleId: Long): R<Map<String, Any>> {
        val userId = LoginHelper.getUserId() ?: 0L
        val menus = menuService.selectMenuList(userId)
        val selectVo = mapOf(
            "checkedKeys" to menuService.selectMenuListByRoleId(roleId),
            "menus" to menuService.buildMenuTreeSelect(menus)
        )
        return R.ok(selectVo)
    }

    /**
     * 新增菜单
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated @RequestBody menu: SysMenuBo): R<Void> {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.fail("新增菜单'" + menu.menuName + "'失败，菜单名称已存在")
        } else if (SystemConstants.YES_FRAME == menu.isFrame && !StringUtils.ishttp(menu.path)) {
            return R.fail("新增菜单'" + menu.menuName + "'失败，地址必须以http(s)://开头")
        } else if (!menuService.checkRouteConfigUnique(menu)) {
            return R.fail("新增菜单'" + menu.menuName + "'失败，路由名称或地址已存在")
        }
        return toAjax(menuService.insertMenu(menu))
    }

    /**
     * 修改菜单
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated @RequestBody menu: SysMenuBo): R<Void> {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.fail("修改菜单'" + menu.menuName + "'失败，菜单名称已存在")
        } else if (SystemConstants.YES_FRAME == menu.isFrame && !StringUtils.ishttp(menu.path)) {
            return R.fail("修改菜单'" + menu.menuName + "'失败，地址必须以http(s)://开头")
        } else if (menu.menuId == menu.parentId) {
            return R.fail("修改菜单'" + menu.menuName + "'失败，上级菜单不能选择自己")
        } else if (!menuService.checkRouteConfigUnique(menu)) {
            return R.fail("修改菜单'" + menu.menuName + "'失败，路由名称或地址已存在")
        }
        return toAjax(menuService.updateMenu(menu))
    }

    /**
     * 删除菜单
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:remove")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    fun remove(@PathVariable menuId: Long): R<Void> {
        if (menuService.hasChildByMenuId(menuId)) {
            return R.fail("存在子菜单,不允许删除")
        }
        if (menuService.checkMenuExistRole(menuId)) {
            return R.fail("菜单已分配,不允许删除")
        }
        return toAjax(menuService.deleteMenu(menuId))
    }

    /**
     * 获取租户套餐菜单树下拉列表
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:query")
    @GetMapping("/tenantPackageMenuTreeselect/{packageId}")
    fun tenantPackageMenuTreeselect(@PathVariable packageId: Long): R<MenuTreeSelectVo> {
        val userId = LoginHelper.getUserId() ?: 0L
        val menus = menuService.selectMenuList(userId)
        val list = menuService.buildMenuTreeSelect(menus)

        // 删除租户管理菜单（菜单ID=6）
        val filteredList = list.filter { it.id != 6L }

        val ids = if (packageId > 0L) {
            menuService.selectMenuListByPackageId(packageId)
        } else {
            emptyList()
        }

        return R.ok(MenuTreeSelectVo(ids, filteredList))
    }

    /**
     * 批量级联删除菜单
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:remove")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/cascade/{menuIds}")
    fun removeCascade(@PathVariable menuIds: Array<Long>): R<Void> {
        val menuIdList = menuIds.toList()

        if (menuService.hasChildByMenuId(menuIdList)) {
            return R.warn("存在子菜单,不允许删除")
        }

        menuService.deleteMenuById(menuIdList)
        return R.ok()
    }

    /**
     * 菜单树选择VO
     */
    data class MenuTreeSelectVo(
        val checkedKeys: List<Long>,
        val menus: List<Tree<Long>>
    )
}
