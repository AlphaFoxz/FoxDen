package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import com.github.alphafoxz.foxden.common.core.constant.TenantConstants
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysMenuBo
import com.github.alphafoxz.foxden.domain.system.service.SysMenuService
import com.github.alphafoxz.foxden.domain.system.service.extensions.deleteMenu
import com.github.alphafoxz.foxden.domain.system.vo.RouterVo
import com.github.alphafoxz.foxden.domain.system.vo.SysMenuVo
import cn.hutool.core.lang.tree.Tree
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
    private val menuService: SysMenuService
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
    @SaCheckRole(
        value = [TenantConstants.SUPER_ADMIN_ROLE_KEY, TenantConstants.TENANT_ADMIN_ROLE_KEY],
        mode = SaMode.OR
    )
    @SaCheckPermission("system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    fun add(@Validated @RequestBody menu: SysMenuBo): R<Void> {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.fail("新增菜单'" + menu.menuName + "'失败，菜单名称已存在")
        }
        return toAjax(menuService.insertMenu(menu))
    }

    /**
     * 修改菜单
     */
    @SaCheckRole(
        value = [TenantConstants.SUPER_ADMIN_ROLE_KEY, TenantConstants.TENANT_ADMIN_ROLE_KEY],
        mode = SaMode.OR
    )
    @SaCheckPermission("system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    fun edit(@Validated @RequestBody menu: SysMenuBo): R<Void> {
        if (!menuService.checkMenuNameUnique(menu)) {
            return R.fail("修改菜单'" + menu.menuName + "'失败，菜单名称已存在")
        }
        return toAjax(menuService.updateMenu(menu))
    }

    /**
     * 删除菜单
     */
    @SaCheckRole(
        value = [TenantConstants.SUPER_ADMIN_ROLE_KEY, TenantConstants.TENANT_ADMIN_ROLE_KEY],
        mode = SaMode.OR
    )
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
}
