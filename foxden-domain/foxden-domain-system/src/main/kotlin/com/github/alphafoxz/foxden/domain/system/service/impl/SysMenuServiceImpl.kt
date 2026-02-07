package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysMenuService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.domain.system.bo.SysMenuBo
import com.github.alphafoxz.foxden.domain.system.vo.RouterVo
import com.github.alphafoxz.foxden.domain.system.vo.SysMenuVo

/**
 * Menu 业务层处理
 */
import com.github.alphafoxz.foxden.domain.system.entity.SysMenu
import cn.hutool.core.lang.tree.Tree
@Service
class SysMenuServiceImpl(): SysMenuService {

    override fun selectMenuList(userId: Long): List<SysMenuVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectMenuList(menu: SysMenuBo, userId: Long): List<SysMenuVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectMenuPermsByUserId(userId: Long): Set<String> {
    return emptySet()
        // TODO: 实现业务逻辑
    }

    override fun selectMenuPermsByRoleId(roleId: Long): Set<String> {
    return emptySet()
        // TODO: 实现业务逻辑
    }

    override fun selectMenuTreeByUserId(userId: Long): List<SysMenu> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectMenuListByRoleId(roleId: Long): List<Long> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectMenuListByPackageId(packageId: Long): List<Long> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun buildMenus(menus: List<SysMenu>): List<RouterVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun buildMenuTreeSelect(menus: List<SysMenuVo>): List<Tree<Long>> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectMenuById(menuId: Long): SysMenuVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun hasChildByMenuId(menuId: Long): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun hasChildByMenuId(menuIds: List<Long>): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun checkMenuExistRole(menuId: Long): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun insertMenu(bo: SysMenuBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateMenu(bo: SysMenuBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteMenuById(menuId: Long): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteMenuById(menuIds: List<Long>) {
        // TODO: 实现业务逻辑
    }

    override fun checkMenuNameUnique(menu: SysMenuBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }
}
