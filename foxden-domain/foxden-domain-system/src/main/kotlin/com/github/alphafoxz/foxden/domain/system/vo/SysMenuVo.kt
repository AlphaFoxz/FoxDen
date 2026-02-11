package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 菜单权限视图对象 sys_menu
 *
 * @author Michelle.Chung
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysMenuVo(
    /**
     * 菜单ID
     */
    var menuId: Long? = null,

    /**
     * 父菜单ID
     */
    var parentId: Long? = null,

    /**
     * 菜单名称
     */
    var menuName: String? = null,

    /**
     * 显示顺序
     */
    var orderNum: Int? = null,

    /**
     * 路由地址
     */
    var path: String? = null,

    /**
     * 组件路径
     */
    var component: String? = null,

    /**
     * 路由参数
     */
    var queryParam: String? = null,

    /**
     * 是否为外链（0是 1否）
     */
    var isFrame: String? = null,

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    var isCache: String? = null,

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    var menuType: String? = null,

    /**
     * 菜单状态（0显示 1隐藏）
     */
    var visible: String? = null,

    /**
     * 菜单状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 权限标识
     */
    var perms: String? = null,

    /**
     * 菜单图标
     */
    var icon: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null,

    /**
     * 创建部门ID
     */
    var createDept: Long? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 子菜单
     */
    var children: List<SysMenuVo>? = null
)
