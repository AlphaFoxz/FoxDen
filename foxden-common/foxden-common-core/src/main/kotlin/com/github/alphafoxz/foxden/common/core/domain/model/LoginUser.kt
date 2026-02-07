package com.github.alphafoxz.foxden.common.core.domain.model

import com.github.alphafoxz.foxden.common.core.domain.dto.PostDTO
import com.github.alphafoxz.foxden.common.core.domain.dto.RoleDTO
import java.io.Serializable

/**
 * 登录用户身份权限
 */
open class LoginUser(
    /**
     * 租户ID
     */
    var tenantId: String? = null,

    /**
     * 用户ID
     */
    var userId: Long? = null,

    /**
     * 部门ID
     */
    var deptId: Long? = null,

    /**
     * 部门类别编码
     */
    var deptCategory: String? = null,

    /**
     * 部门名
     */
    var deptName: String? = null,

    /**
     * 用户唯一标识
     */
    var token: String? = null,

    /**
     * 用户类型
     */
    var userType: String? = null,

    /**
     * 登录时间
     */
    var loginTime: Long? = null,

    /**
     * 过期时间
     */
    var expireTime: Long? = null,

    /**
     * 登录IP地址
     */
    var ipaddr: String? = null,

    /**
     * 登录地点
     */
    var loginLocation: String? = null,

    /**
     * 浏览器类型
     */
    var browser: String? = null,

    /**
     * 操作系统
     */
    var os: String? = null,

    /**
     * 菜单权限
     */
    var menuPermission: Set<String> = emptySet(),

    /**
     * 角色权限
     */
    var rolePermission: Set<String> = emptySet(),

    /**
     * 用户名
     */
    var username: String? = null,

    /**
     * 用户昵称
     */
    var nickname: String? = null,

    /**
     * 角色对象
     */
    var roles: List<RoleDTO> = emptyList(),

    /**
     * 岗位对象
     */
    var posts: List<PostDTO> = emptyList(),

    /**
     * 数据权限 当前角色ID
     */
    var roleId: Long? = null,

    /**
     * 客户端
     */
    var clientKey: String? = null,

    /**
     * 设备类型
     */
    var deviceType: String? = null
) : Serializable {

    /**
     * 获取登录id
     */
    val loginId: String
        get() {
            if (userType == null) {
                throw IllegalArgumentException("用户类型不能为空")
            }
            if (userId == null) {
                throw IllegalArgumentException("用户ID不能为空")
            }
            return "$userType:$userId"
        }

    companion object {
        private const val serialVersionUID = 1L
    }
}
