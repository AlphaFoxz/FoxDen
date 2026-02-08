package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 菜单权限表 sys_menu
 */
@Entity
@Table(name = "sys_menu")
interface SysMenu : CommInfo {
    /**
     * 主键ID
     */
    @org.babyfish.jimmer.sql.Column(name = "menu_id")
    @Id
    @GeneratedValue
    val id: Long

    /**
     * 父菜单ID
     */
    @IdView
    val parentId: Long?

    /**
     * 菜单名称
     */
    val menuName: String

    /**
     * 显示顺序
     */
    val orderNum: Int?

    /**
     * 路由地址
     */
    val path: String?

    /**
     * 组件路径
     */
    val component: String?

    /**
     * 路由参数
     */
    val queryParam: String?

    /**
     * 是否为外链（0是 1否）
     */
    @Column(name = "is_frame")
    val frameFlag: String?

    /**
     * 是否缓存（0缓存 1不缓存）
     */
    @Column(name = "is_cache")
    val cacheFlag: String?

    /**
     * 类型（M目录 C菜单 F按钮）
     */
    val menuType: String?

    /**
     * 显示状态（0显示 1隐藏）
     */
    val visible: String?

    /**
     * 菜单状态（0正常 1停用）
     */
    val status: String?

    /**
     * 权限字符串
     */
    val perms: String?

    /**
     * 菜单图标
     */
    val icon: String?

    /**
     * 父菜单
     */
    @ManyToOne
    @JoinColumn(name = "parent_id")
    val parent: SysMenu?

    /**
     * 子菜单
     */
    @OneToMany(mappedBy = "parent")
    val children: List<SysMenu>
}
