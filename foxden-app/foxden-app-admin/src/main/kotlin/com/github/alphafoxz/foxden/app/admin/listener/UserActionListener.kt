package com.github.alphafoxz.foxden.app.admin.listener

import cn.dev33.satoken.listener.SaTokenListener
import cn.dev33.satoken.stp.StpUtil
import cn.dev33.satoken.stp.parameter.SaLoginParameter
import cn.hutool.core.convert.Convert
import cn.hutool.http.useragent.UserAgent
import cn.hutool.http.useragent.UserAgentUtil
import com.github.alphafoxz.foxden.common.core.constant.CacheConstants
import com.github.alphafoxz.foxden.common.core.constant.Constants
import com.github.alphafoxz.foxden.common.core.domain.dto.UserOnlineDTO
import com.github.alphafoxz.foxden.common.core.utils.MessageUtils
import com.github.alphafoxz.foxden.common.web.utils.ServletUtils
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.AddressUtils
import com.github.alphafoxz.foxden.common.log.event.LogininforEvent
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.jimmer.helper.TenantHelper
import com.github.alphafoxz.foxden.app.admin.service.SysLoginService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * 用户行为 侦听器的实现
 *
 * @author FoxDen Team
 */
@Component
class UserActionListener(
    private val loginService: SysLoginService
) : SaTokenListener {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 每次登录时触发
     */
    override fun doLogin(loginType: String, loginId: Any, tokenValue: String, loginParameter: SaLoginParameter) {
        val userAgent = UserAgentUtil.parse(ServletUtils.getRequest().getHeader("User-Agent"))
        val ip = ServletUtils.getClientIP()

        val dto = UserOnlineDTO().apply {
            ipaddr = ip
            loginLocation = AddressUtils.getRealAddressByIP(ip)
            browser = userAgent.browser.name
            os = userAgent.os.name
            loginTime = System.currentTimeMillis()
            tokenId = tokenValue
            userName = loginParameter.getExtra(LoginHelper.USER_NAME_KEY) as? String
            clientKey = loginParameter.getExtra(LoginHelper.CLIENT_KEY) as? String
            deviceType = loginParameter.deviceType
            deptName = loginParameter.getExtra(LoginHelper.DEPT_NAME_KEY) as? String
        }

        val tenantId = loginParameter.getExtra(LoginHelper.TENANT_KEY) as? String
        TenantHelper.dynamicTenant(tenantId ?: "") {
            if (loginParameter.timeout == -1L) {
                RedisUtils.setCacheObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, dto)
            } else {
                RedisUtils.setCacheObject(
                    CacheConstants.ONLINE_TOKEN_KEY + tokenValue,
                    dto,
                    Duration.ofSeconds(loginParameter.timeout)
                )
            }
        }

        // 记录登录日志
        val logininforEvent = LogininforEvent().apply {
            this.tenantId = tenantId
            username = dto.userName
            status = Constants.LOGIN_SUCCESS
            message = MessageUtils.message("user.login.success")
            request = ServletUtils.getRequest()
        }
        SpringUtils.context().publishEvent(logininforEvent)

        // 更新登录信息
        loginService.recordLoginInfo(loginParameter.getExtra(LoginHelper.USER_KEY) as? Long ?: 0L, ip)
        log.info("user doLogin, userId:{}, token:{}", loginId, tokenValue)
    }

    /**
     * 每次注销时触发
     */
    override fun doLogout(loginType: String, loginId: Any, tokenValue: String) {
        val tenantId = Convert.toStr(StpUtil.getExtra(tokenValue, LoginHelper.TENANT_KEY))
        TenantHelper.dynamicTenant(tenantId ?: "") {
            RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue)
        }
        log.info("user doLogout, userId:{}, token:{}", loginId, tokenValue)
    }

    /**
     * 每次被踢下线时触发
     */
    override fun doKickout(loginType: String, loginId: Any, tokenValue: String) {
        val tenantId = Convert.toStr(StpUtil.getExtra(tokenValue, LoginHelper.TENANT_KEY))
        TenantHelper.dynamicTenant(tenantId ?: "") {
            RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue)
        }
        log.info("user doKickout, userId:{}, token:{}", loginId, tokenValue)
    }

    /**
     * 每次被顶下线时触发
     */
    override fun doReplaced(loginType: String, loginId: Any, tokenValue: String) {
        val tenantId = Convert.toStr(StpUtil.getExtra(tokenValue, LoginHelper.TENANT_KEY))
        TenantHelper.dynamicTenant(tenantId ?: "") {
            RedisUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue)
        }
        log.info("user doReplaced, userId:{}, token:{}", loginId, tokenValue)
    }

    /**
     * 每次被封禁时触发
     */
    override fun doDisable(loginType: String, loginId: Any, service: String, level: Int, disableTime: Long) {
    }

    /**
     * 每次被解封时触发
     */
    override fun doUntieDisable(loginType: String, loginId: Any, service: String) {
    }

    /**
     * 每次打开二级认证时触发
     */
    override fun doOpenSafe(loginType: String, tokenValue: String, service: String, safeTime: Long) {
    }

    /**
     * 每次创建Session时触发
     */
    override fun doCloseSafe(loginType: String, tokenValue: String, service: String) {
    }

    /**
     * 每次创建Session时触发
     */
    override fun doCreateSession(id: String) {
    }

    /**
     * 每次注销Session时触发
     */
    override fun doLogoutSession(id: String) {
    }

    /**
     * 每次Token续期时触发
     */
    override fun doRenewTimeout(loginType: String, loginId: Any, tokenValue: String, timeout: Long) {
    }
}
