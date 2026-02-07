package com.github.alphafoxz.foxden.app.admin.service

import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.lock.annotation.Lock4j
import com.github.alphafoxz.foxden.common.core.constant.CacheConstants
import com.github.alphafoxz.foxden.common.core.constant.Constants
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.constant.TenantConstants
import com.github.alphafoxz.foxden.common.core.domain.dto.PostDTO
import com.github.alphafoxz.foxden.common.core.domain.dto.RoleDTO
import com.github.alphafoxz.foxden.common.core.domain.model.LoginUser
import com.github.alphafoxz.foxden.common.core.enums.LoginType
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.exception.user.UserException
import com.github.alphafoxz.foxden.common.core.utils.DateUtils
import com.github.alphafoxz.foxden.common.core.utils.MessageUtils
import com.github.alphafoxz.foxden.common.web.utils.ServletUtils
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.jimmer.helper.DataPermissionHelper
import com.github.alphafoxz.foxden.common.log.event.LogininforEvent
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.core.exception.tenant.TenantException
import com.github.alphafoxz.foxden.common.jimmer.helper.TenantHelper
import com.github.alphafoxz.foxden.domain.system.entity.SysUser
import com.github.alphafoxz.foxden.domain.system.entity.SysUserFetcher
import com.github.alphafoxz.foxden.domain.system.service.SysDeptService
import com.github.alphafoxz.foxden.domain.system.service.SysPermissionService
import com.github.alphafoxz.foxden.domain.system.service.SysPostService
import com.github.alphafoxz.foxden.domain.system.service.SysRoleService
import com.github.alphafoxz.foxden.domain.system.service.SysSocialService
import com.github.alphafoxz.foxden.domain.system.service.SysTenantService
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import com.github.alphafoxz.foxden.domain.system.service.extensions.insertByBo
import com.github.alphafoxz.foxden.domain.system.service.extensions.queryList
import com.github.alphafoxz.foxden.domain.system.service.extensions.selectPostDetailsByUserId
import com.github.alphafoxz.foxden.domain.system.service.extensions.updateByBo
import com.github.alphafoxz.foxden.domain.system.vo.SysDeptVo
import com.github.alphafoxz.foxden.domain.system.vo.SysPostVo
import com.github.alphafoxz.foxden.domain.system.vo.SysRoleVo
import com.github.alphafoxz.foxden.domain.system.vo.SysTenantVo
import com.github.alphafoxz.foxden.domain.system.vo.SysUserVo
import me.zhyd.oauth.model.AuthUser
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.Date
import java.util.function.Supplier

/**
 * 登录校验方法
 *
 * @author FoxDen Team
 */
@Service
class SysLoginService(
    @Value("\${user.password.maxRetryCount}")
    private val maxRetryCount: Int,
    @Value("\${user.password.lockTime}")
    private val lockTime: Int,
    private val tenantService: SysTenantService,
    private val permissionService: SysPermissionService,
    private val roleService: SysRoleService,
    private val socialService: SysSocialService,
    private val deptService: SysDeptService,
    private val postService: SysPostService,
    private val userService: SysUserService
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 绑定第三方用户
     *
     * @param authUserData 授权响应实体
     */
    @Lock4j
    fun socialRegister(authUserData: AuthUser) {
        val authId = authUserData.source + authUserData.uuid
        val userId = LoginHelper.getUserId()

        // 构建社交登录用户数据
        val socialData: MutableMap<String, Any?> = mutableMapOf(
            "userId" to userId,
            "authId" to authId,
            "source" to authUserData.source,
            "openId" to authUserData.uuid,
            "userName" to authUserData.username,
            "nickName" to authUserData.nickname
            // 其他token信息...
        )

        // 检查是否已绑定
        val checkList = socialService.selectByAuthId(authId)
        if (checkList.isNotEmpty()) {
            throw ServiceException("此三方账号已经被绑定!")
        }

        // 查询是否已经绑定用户
        val existingList = socialService.queryList(mapOf(
            "userId" to userId,
            "source" to authUserData.source
        ))

        if (existingList.isEmpty()) {
            // 没有绑定用户，新增用户信息
            socialService.insertByBo(socialData)
        } else {
            // 更新用户信息
            socialData["socialId"] = existingList[0].socialId
            socialService.updateByBo(socialData)
        }
    }

    /**
     * 退出登录
     */
    fun logout() {
        try {
            val loginUser = LoginHelper.getLoginUser()
            if (loginUser != null) {
                if (TenantHelper.isEnable() && LoginHelper.isSuperAdmin()) {
                    // 超级管理员 登出清除动态租户
                    TenantHelper.clearDynamic()
                }
                recordLogininfor(
                    loginUser.tenantId ?: "", loginUser.username ?: "",
                    Constants.LOGOUT, MessageUtils.message("user.logout.success") ?: "用户退出成功"
                )
            }
        } catch (ignored: NotLoginException) {
        } finally {
            try {
                StpUtil.logout()
            } catch (ignored: NotLoginException) {
            }
        }
    }

    /**
     * 记录登录信息
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     */
    fun recordLogininfor(tenantId: String?, username: String?, status: String, message: String) {
        val logininforEvent = LogininforEvent().apply {
            this.tenantId = tenantId
            this.username = username
            this.status = status
            this.message = message
            this.request = ServletUtils.getRequest()
        }
        SpringUtils.context().publishEvent(logininforEvent)
    }

    /**
     * 构建登录用户
     */
    fun buildLoginUser(user: SysUserVo): LoginUser {
        val userId = user.userId ?: 0L

        // 先获取用户角色
        val userRoles = roleService.selectRolesByUserId(userId)
        val roleIds = userRoles.mapNotNull { it.roleId }.toTypedArray()

        val loginUser = LoginUser().apply {
            tenantId = user.tenantId
            this.userId = userId
            deptId = user.deptId
            username = user.userName
            nickname = user.nickName
            userType = user.userType
            menuPermission = permissionService.getMenuPermission(userId, roleIds)
            rolePermission = permissionService.getRolePermission(userId, roleIds)
        }

        // 设置部门信息
        if (user.deptId != null) {
            val dept = deptService.selectDeptById(user.deptId!!)
            loginUser.deptName = dept?.deptName ?: StringUtils.EMPTY
            loginUser.deptCategory = dept?.deptCategory ?: StringUtils.EMPTY
        }

        // 设置角色和岗位
        val roles = roleService.selectRolesByUserId(userId)
        val posts = postService.selectPostDetailsByUserId(userId)
        loginUser.roles = roles.map { role ->
            RoleDTO().apply {
                roleId = role.roleId
                roleName = role.roleName
                roleKey = role.roleKey
                roleSort = role.roleSort
                dataScope = role.dataScope
                menuCheckStrictly = role.menuCheckStrictly
                deptCheckStrictly = role.deptCheckStrictly
                status = role.status
                delFlag = role.delFlag
                createBy = role.createBy
                createTime = role.createTime
                updateBy = role.updateBy
                updateTime = role.updateTime
                remark = role.remark
            }
        }
        loginUser.posts = posts.map { post ->
            PostDTO().apply {
                postId = post.postId
                postCode = post.postCode
                postName = post.postName
                postSort = post.postSort
                status = post.status
                remark = post.remark
                createBy = post.createBy
                createTime = post.createTime
                updateBy = post.updateBy
                updateTime = post.updateTime
            }
        }

        return loginUser
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     * @param ip     IP地址
     */
    fun recordLoginInfo(userId: Long, ip: String) {
        val userUpdate = mapOf(
            "userId" to userId,
            "loginIp" to ip,
            "loginDate" to DateUtils.getNowDate()
        )
        DataPermissionHelper.ignore {
            userService.updateByBo(userUpdate)
        }
    }

    /**
     * 登录校验
     */
    fun checkLogin(loginType: LoginType, tenantId: String?, username: String, supplier: Supplier<Boolean>) {
        val errorKey = CacheConstants.PWD_ERR_CNT_KEY + username
        val loginFail = Constants.LOGIN_FAIL

        // 获取用户登录错误次数
        val errorNumber = RedisUtils.getCacheObject(errorKey) ?: 0

        // 锁定时间内登录 则踢出
        if (errorNumber >= maxRetryCount) {
            recordLogininfor(tenantId ?: "", username ?: "", loginFail,
                MessageUtils.message(loginType.retryLimitExceed, maxRetryCount, lockTime) ?: "超过最大尝试次数")
            throw UserException(loginType.retryLimitExceed, maxRetryCount, lockTime)
        }

        if (supplier.get()) {
            // 错误次数递增
            val newErrorNumber = errorNumber + 1
            RedisUtils.setCacheObject(errorKey, newErrorNumber, Duration.ofMinutes(lockTime.toLong()))

            // 达到规定错误次数 则锁定登录
            if (newErrorNumber >= maxRetryCount) {
                recordLogininfor(tenantId ?: "", username ?: "", loginFail,
                    MessageUtils.message(loginType.retryLimitExceed, maxRetryCount, lockTime) ?: "超过最大尝试次数")
                throw UserException(loginType.retryLimitExceed, maxRetryCount, lockTime)
            } else {
                recordLogininfor(tenantId ?: "", username ?: "", loginFail,
                    MessageUtils.message(loginType.retryLimitCount, newErrorNumber) ?: "登录失败次数过多")
                throw UserException(loginType.retryLimitCount, newErrorNumber)
            }
        }

        // 登录成功 清空错误次数
        RedisUtils.deleteObject(errorKey)
    }

    /**
     * 校验租户
     *
     * @param tenantId 租户ID
     */
    fun checkTenant(tenantId: String?) {
        if (!TenantHelper.isEnable()) {
            return
        }
        if (StringUtils.isBlank(tenantId)) {
            throw TenantException("tenant.number.not.blank")
        }
        if (TenantConstants.DEFAULT_TENANT_ID == tenantId) {
            return
        }
        val tenant = tenantService.queryByTenantId(tenantId ?: "")
        if (tenant == null) {
            log.info("登录租户：{} 不存在.", tenantId)
            throw TenantException("tenant.not.exists")
        } else if (SystemConstants.DISABLE == tenant.status) {
            log.info("登录租户：{} 已被停用.", tenantId)
            throw TenantException("tenant.blocked")
        } else if (tenant.expireTime != null && DateUtils.getNowDate().after(java.sql.Timestamp.valueOf(tenant.expireTime))) {
            log.info("登录租户：{} 已超过有效期.", tenantId)
            throw TenantException("tenant.expired")
        }
    }
}
