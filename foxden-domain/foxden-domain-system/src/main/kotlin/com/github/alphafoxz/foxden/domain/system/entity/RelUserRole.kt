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
 * 用户和角色关联
 * 
 * @author wong
 */
@Entity
@Table(name = "rel_user_role")
interface RelUserRole {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    @get:Max(value = 9223372036854775807, message = "用户ID不可大于9223372036854775807")
    @get:Min(value = 0, message = "用户ID不可小于0")
    val userId: Long

    /**
     * 角色ID
     */
    @Column(name = "role_id")
    @get:Max(value = 9223372036854775807, message = "角色ID不可大于9223372036854775807")
    @get:Min(value = 0, message = "角色ID不可小于0")
    val roleId: Long
}
