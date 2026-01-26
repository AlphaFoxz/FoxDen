package com.github.alphafoxz.foxden.domain.system.entity

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.GenerationType
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.Table

/**
 * 角色和菜单关联
 * 
 * @author wong
 */
@Entity
@Table(name = "rel_role_menu")
interface RelRoleMenu {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long

    /**
     * 角色ID
     */
    @Column(name = "role_id")
    @get:Max(value = 9223372036854775807, message = "角色ID不可大于9223372036854775807")
    @get:Min(value = 0, message = "角色ID不可小于0")
    val roleId: Long

    /**
     * 菜单ID
     */
    @Column(name = "menu_id")
    @get:Max(value = 9223372036854775807, message = "菜单ID不可大于9223372036854775807")
    @get:Min(value = 0, message = "菜单ID不可小于0")
    val menuId: Long
}
