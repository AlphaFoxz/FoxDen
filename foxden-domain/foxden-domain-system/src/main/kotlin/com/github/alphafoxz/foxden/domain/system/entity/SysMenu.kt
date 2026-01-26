package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommInfo
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.hibernate.validator.constraints.Length

/**
 * 菜单权限
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_menu")
interface SysMenu : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 菜单名称
     */
    @Column(name = "menu_name")
    @get:Length(max = 2147483647)
    val menuName: String

    /**
     * 父菜单ID
     */
    @Column(name = "parent_id")
    @get:Max(value = 9223372036854775807, message = "父菜单ID不可大于9223372036854775807")
    @get:Min(value = 0, message = "父菜单ID不可小于0")
    val parentId: Long?

    /**
     * 显示顺序
     */
    @Column(name = "order_num")
    @get:Max(value = 2147483647, message = "显示顺序不可大于2147483647")
    @get:Min(value = 0, message = "显示顺序不可小于0")
    val orderNum: Int?

    /**
     * 路由地址
     */
    @Column(name = "path")
    @get:Length(max = 2147483647)
    val path: String?

    /**
     * 组件路径
     */
    @Column(name = "component")
    @get:Length(max = 2147483647)
    val component: String?

    /**
     * 路由参数
     */
    @Column(name = "query_param")
    @get:Length(max = 2147483647)
    val queryParam: String?

    /**
     * 是否为外链
     */
    @Column(name = "frame")
    val frame: Boolean?

    /**
     * 是否缓存
     */
    @Column(name = "cache")
    val cache: Boolean?

    /**
     * 菜单类型（M目录 C菜单 F按钮）
     */
    @Column(name = "menu_type")
    @get:Length(max = 1)
    val menuType: String

    /**
     * 显示状态（0显示 1隐藏）
     */
    @Column(name = "visible")
    @get:Length(max = 1)
    val visible: String?

    /**
     * 菜单状态（0正常 1停用）
     */
    @Column(name = "status")
    @get:Length(max = 1)
    val status: String

    /**
     * 权限标识
     */
    @Column(name = "perms")
    @get:Length(max = 2147483647)
    val perms: String?

    /**
     * 菜单图标
     */
    @Column(name = "icon")
    @get:Length(max = 2147483647)
    val icon: String?

    /**
     * 备注
     */
    @Column(name = "remark")
    @get:Length(max = 2147483647)
    val remark: String?
}
