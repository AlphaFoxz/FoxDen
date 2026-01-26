package com.github.alphafoxz.foxden.domain.system.entity

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.babyfish.jimmer.sql.*

/**
 * 角色和部门关联
 * 
 * @author wong
 */
@Entity
@Table(name = "rel_role_dept")
interface RelRoleDept {
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
     * 部门ID
     */
    @Column(name = "dept_id")
    @get:Max(value = 9223372036854775807, message = "部门ID不可大于9223372036854775807")
    @get:Min(value = 0, message = "部门ID不可小于0")
    val deptId: Long
}
