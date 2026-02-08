package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*
import java.util.Date

/**
 * 用户对象 sys_user
 */
@Entity
@Table(name = "sys_user")
interface SysUser : CommDelFlag, CommInfo, CommTenant {
    /**
     * 主键ID
     */
    @org.babyfish.jimmer.sql.Column(name = "user_id")
    @Id
    @GeneratedValue
    val id: Long

    /**
     * 部门ID
     */
    @IdView
    val deptId: Long?

    /**
     * 用户账号
     */
    val userName: String

    /**
     * 用户昵称
     */
    val nickName: String?

    /**
     * 用户类型（sys_user系统用户）
     */
    val userType: String?

    /**
     * 用户邮箱
     */
    val email: String?

    /**
     * 手机号码
     */
    val phonenumber: String?

    /**
     * 用户性别
     */
    val sex: String?

    /**
     * 用户头像
     */
    val avatar: Long?

    /**
     * 密码
     */
    val password: String?

    /**
     * 帐号状态（0正常 1停用）
     */
    val status: String?

    /**
     * 最后登录IP
     */
    val loginIp: String?

    /**
     * 最后登录时间
     */
    val loginDate: Date?

    /**
     * 角色
     */
    @ManyToMany
    @JoinTable(name = "sys_user_role")
    val roles: List<SysRole>

    /**
     * 岗位
     */
    @ManyToMany
    @JoinTable(name = "sys_user_post")
    val posts: List<SysPost>

    /**
     * 部门
     */
    @ManyToOne
    @JoinColumn(name = "dept_id")
    val dept: SysDept?
}
