package com.github.alphafoxz.foxden.domain.system.bo

import com.fasterxml.jackson.annotation.JsonInclude
import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * 菜单权限业务对象 sys_menu
 *
 * @author Michelle.Chung
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysMenuBo(
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
    @get:NotBlank(message = "菜单名称不能为空", groups = [AddGroup::class, EditGroup::class])
    @get:Size(min = 0, max = 50, message = "菜单名称长度不能超过{max}个字符")
    var menuName: String? = null,

    /**
     * 显示顺序
     */
    @get:NotNull(message = "显示顺序不能为空", groups = [AddGroup::class, EditGroup::class])
    var orderNum: Int? = null,

    /**
     * 路由地址
     */
    @get:Size(min = 0, max = 200, message = "路由地址不能超过{max}个字符")
    var path: String? = null,

    /**
     * 组件路径
     */
    @get:Size(min = 0, max = 200, message = "组件路径不能超过{max}个字符")
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
    @get:NotBlank(message = "菜单类型不能为空", groups = [AddGroup::class, EditGroup::class])
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
    @get:Size(min = 0, max = 100, message = "权限标识长度不能超过{max}个字符")
    var perms: String? = null,

    /**
     * 菜单图标
     */
    var icon: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null,

    /**
     * 更新者
     */
    var updateBy: String? = null,

    /**
     * 更新时间
     */
    var updateTime: java.time.LocalDateTime? = null
) {
    companion object {
        const val TYPE_DIR: String = "M"
        const val TYPE_MENU: String = "C"
        const val TYPE_BUTTON: String = "F"
    }
}
