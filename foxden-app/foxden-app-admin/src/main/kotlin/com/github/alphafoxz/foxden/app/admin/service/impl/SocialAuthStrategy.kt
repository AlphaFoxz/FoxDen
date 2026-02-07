package com.github.alphafoxz.foxden.app.admin.service.impl

import cn.dev33.satoken.stp.StpUtil
import cn.dev33.satoken.stp.parameter.SaLoginParameter
import com.github.alphafoxz.foxden.app.admin.domain.vo.LoginVo
import com.github.alphafoxz.foxden.app.admin.service.AuthStrategy
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.domain.model.LoginUser
import com.github.alphafoxz.foxden.common.core.domain.model.SocialLoginBody
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.exception.user.UserException
import com.github.alphafoxz.foxden.common.core.utils.StreamUtils
import com.github.alphafoxz.foxden.common.core.utils.ValidatorUtils
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.security.utils.tokenValue
import com.github.alphafoxz.foxden.common.security.utils.tokenTimeout
import com.github.alphafoxz.foxden.common.jimmer.helper.TenantHelper
import com.github.alphafoxz.foxden.domain.system.service.SysSocialService
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import com.github.alphafoxz.foxden.domain.system.vo.SysClientVo
import com.github.alphafoxz.foxden.domain.system.vo.SysSocialVo
import com.github.alphafoxz.foxden.domain.system.vo.SysUserVo
import me.zhyd.oauth.model.AuthResponse
import me.zhyd.oauth.model.AuthUser
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 第三方授权策略
 *
 * @author FoxDen Team
 */
@Service("social${AuthStrategy.BASE_NAME}")
class SocialAuthStrategy(
    private val socialService: SysSocialService,
    private val userService: SysUserService,
    private val sysLoginService: com.github.alphafoxz.foxden.app.admin.service.SysLoginService,
    private val socialProperties: com.github.alphafoxz.foxden.common.social.config.properties.SocialProperties
) : AuthStrategy {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun login(body: String, client: SysClientVo): LoginVo {
        val loginBody = JsonUtils.parseObject(body, SocialLoginBody::class.java)
        ValidatorUtils.validate(loginBody)

        // 调用社交登录工具类进行认证
        val response: AuthResponse<AuthUser> = com.github.alphafoxz.foxden.common.social.utils.SocialUtils.loginAuth(
            loginBody!!.source ?: "", loginBody!!.socialCode ?: "",
            loginBody!!.socialState ?: "", socialProperties
        )

        if (!response.ok()) {
            throw ServiceException(response.msg ?: "社交登录认证失败")
        }

        val authUserData = response.data
        val authId = authUserData.source + authUserData.uuid

        // 查询已绑定的社交账号
        val list = socialService.selectByAuthId(authId)
        if (list.isEmpty()) {
            throw ServiceException("你还没有绑定第三方账号，绑定后才可以登录！")
        }

        // 多租户支持
        val social: SysSocialVo = if (TenantHelper.isEnable()) {
            val tenantId = loginBody!!.tenantId ?: ""
            val found = list.find { it.tenantId == tenantId }
            if (found == null) {
                throw ServiceException("对不起，你没有权限登录当前租户！")
            }
            found
        } else {
            list[0]
        }

        val loginUser = TenantHelper.dynamic(social.tenantId ?: "") {
            val user = loadUser(social.userId ?: 0L)
            sysLoginService.buildLoginUser(user)
        }

        loginUser.clientKey = client.clientKey
        loginUser.deviceType = client.deviceType

        val model = SaLoginParameter()
        model.deviceType = client.deviceType
        model.setTimeout(client.timeout ?: -1L)
        model.setActiveTimeout(client.activeTimeout ?: -1L)
        model.setExtra(LoginHelper.CLIENT_KEY, client.clientId)

        LoginHelper.login(loginUser, model)

        return LoginVo(
            accessToken = tokenValue(),
            expireIn = tokenTimeout(),
            clientId = client.clientId
        )
    }

    private fun loadUser(userId: Long): SysUserVo {
        val user = userService.selectUserById(userId)
        if (user == null) {
            log.info("登录用户：{} 不存在.", userId)
            throw UserException("user.not.exists", "")
        } else if (SystemConstants.DISABLE == user.status) {
            log.info("登录用户：{} 已被停用.", userId)
            throw UserException("user.blocked", "")
        }
        return user
    }
}
