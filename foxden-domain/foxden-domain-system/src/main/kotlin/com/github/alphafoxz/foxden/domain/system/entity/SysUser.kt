package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommInfo
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.LocalDateTime
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.hibernate.validator.constraints.Length

/**
 * 用户信息
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_user")
interface SysUser : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 部门ID
     */
    @Column(name = "dept_id")
    @get:Max(value = 9223372036854775807, message = "部门ID不可大于9223372036854775807")
    @get:Min(value = 0, message = "部门ID不可小于0")
    val deptId: Long?

    /**
     * 用户账号
     */
    @Column(name = "user_name")
    @get:Length(max = 2147483647)
    val userName: String

    /**
     * 用户昵称
     */
    @Column(name = "nick_name")
    @get:Length(max = 2147483647)
    val nickName: String

    /**
     * 用户类型（sys_user系统用户）
     */
    @Column(name = "user_type")
    @get:Length(max = 2147483647)
    val userType: String?

    /**
     * 用户邮箱
     */
    @Column(name = "email")
    @get:Length(max = 2147483647)
    val email: String?

    /**
     * 手机号码
     */
    @Column(name = "phonenumber")
    @get:Length(max = 2147483647)
    val phonenumber: String?

    /**
     * 用户性别（0男 1女 2未知）
     */
    @Column(name = "sex")
    @get:Length(max = 1)
    val sex: String?

    /**
     * 头像地址
     */
    @Column(name = "avatar")
    @get:Max(value = 9223372036854775807, message = "头像地址不可大于9223372036854775807")
    @get:Min(value = 0, message = "头像地址不可小于0")
    val avatar: Long?

    /**
     * 密码
     */
    @Column(name = "password")
    @get:Length(max = 2147483647)
    val password: String

    /**
     * 帐号状态（0正常 1停用）
     */
    @Column(name = "status")
    @get:Length(max = 1)
    val status: String

    /**
     * 最后登陆IP
     */
    @Column(name = "login_ip")
    @get:Length(max = 2147483647)
    val loginIp: String?

    /**
     * 最后登陆时间
     */
    @Column(name = "login_date")
    val loginDate: LocalDateTime?

    /**
     * 备注
     */
    @Column(name = "remark")
    @get:Length(max = 2147483647)
    val remark: String?
}
