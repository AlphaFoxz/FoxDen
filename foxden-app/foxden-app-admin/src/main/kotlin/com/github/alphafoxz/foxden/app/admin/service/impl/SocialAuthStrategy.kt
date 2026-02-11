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
import com.github.alphafoxz.foxden.common.core.utils.ValidatorUtils
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import com.github.alphafoxz.foxden.common.jimmer.helper.TenantHelper
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.security.utils.tokenTimeout
import com.github.alphafoxz.foxden.common.security.utils.tokenValue
import com.github.alphafoxz.foxden.common.social.config.properties.SocialProperties
import com.github.alphafoxz.foxden.common.social.utils.SocialUtils
import com.github.alphafoxz.foxden.app.admin.service.SysLoginService
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import com.github.alphafoxz.foxden.domain.system.service.SysSocialService
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
    private val socialProperties: SocialProperties,
    private val socialService: SysSocialService,
    private val userService: SysUserService,
    private val sysLoginService: SysLoginService
) : AuthStrategy {

    companion object {
        const val BASE_NAME = "social"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun login(body: String, client: SysClientVo): LoginVo {
        val loginBody = JsonUtils.parseObject(body, SocialLoginBody::class.java)
        ValidatorUtils.validate(loginBody)

        val tenantId = loginBody!!.tenantId ?: ""
        val socialSource = loginBody!!.source ?: ""
        val socialCode = loginBody!!.socialCode ?: ""
        val socialState = loginBody!!.socialState ?: ""

        // 第三方登录授权
        val response: AuthResponse<AuthUser> = SocialUtils.loginAuth(
            socialSource, socialCode, socialState, socialProperties
        )
        if (!response.ok()) {
            throw ServiceException(response.msg ?: "第三方登录失败")
        }
        val authUserData = response.data

        // 查询是否已绑定
        val list = socialService.selectByAuthId(authUserData.source + authUserData.uuid)
        if (list.isEmpty()) {
            throw ServiceException("你还没有绑定第三方账号，绑定后才可以登录！")
        }

        // 多租户支持
        val socialLogin: SysSocialVo = if (TenantHelper.isEnable()) {
            val found = list.find { it.tenantId == tenantId }
            if (found == null) {
                throw ServiceException("对不起，你没有权限登录当前租户！")
            }
            found
        } else {
            list[0]
        }

        val loginUser = TenantHelper.dynamicTenant(socialLogin.tenantId ?: "") {
            val userId = socialLogin.userId ?: throw ServiceException("社交登录未绑定用户")
            val user = loadUser(userId)
            // 此处可根据登录用户的数据不同 自行创建 loginUser
            sysLoginService.buildLoginUser(user)
        }

        loginUser.clientKey = client.clientKey
        loginUser.deviceType = client.deviceType

        val model = SaLoginParameter()
        model.deviceType = client.deviceType
        // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
        model.setTimeout(client.timeout ?: -1L)
        model.setActiveTimeout(client.activeTimeout ?: -1L)
        model.setExtra(LoginHelper.CLIENT_KEY, client.clientId)

        // 生成token
        LoginHelper.login(loginUser, model)

        return LoginVo(
            accessToken = tokenValue(),
            expireIn = tokenTimeout(),
            clientId = client.clientId
        )
    }

    /**
     * 加载用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    private fun loadUser(userId: Long): SysUserVo {
        val user = userService.selectUserById(userId)
        if (user == null) {
            log.info("登录用户：{} 不存在.", "")
            throw UserException("user.not.exists", "")
        } else if (SystemConstants.DISABLE == user.status) {
            log.info("登录用户：{} 已被停用.", "")
            throw UserException("user.blocked", "")
        }
        return user
    }
}
