package com.github.alphafoxz.foxden.app.admin.service.impl

import cn.dev33.satoken.stp.StpUtil
import cn.dev33.satoken.stp.parameter.SaLoginParameter
import cn.hutool.crypto.digest.BCrypt
import com.github.alphafoxz.foxden.app.admin.domain.vo.LoginVo
import com.github.alphafoxz.foxden.app.admin.service.AuthStrategy
import com.github.alphafoxz.foxden.common.core.constant.Constants
import com.github.alphafoxz.foxden.common.core.constant.GlobalConstants
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.domain.model.LoginUser
import com.github.alphafoxz.foxden.common.core.domain.model.PasswordLoginBody
import com.github.alphafoxz.foxden.common.core.enums.LoginType
import com.github.alphafoxz.foxden.common.core.exception.user.CaptchaException
import com.github.alphafoxz.foxden.common.core.exception.user.CaptchaExpireException
import com.github.alphafoxz.foxden.common.core.exception.user.UserException
import com.github.alphafoxz.foxden.common.core.utils.MessageUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.core.utils.ValidatorUtils
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.security.utils.tokenValue
import com.github.alphafoxz.foxden.common.security.utils.tokenTimeout
import com.github.alphafoxz.foxden.common.jimmer.helper.TenantHelper
import com.github.alphafoxz.foxden.common.web.config.properties.CaptchaProperties
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import com.github.alphafoxz.foxden.domain.system.vo.SysClientVo
import com.github.alphafoxz.foxden.domain.system.vo.SysUserVo
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Duration

/**
 * 密码认证策略
 *
 * @author FoxDen Team
 */
@Service("password${AuthStrategy.BASE_NAME}")
class PasswordAuthStrategy(
    private val captchaProperties: CaptchaProperties,
    private val sysLoginService: com.github.alphafoxz.foxden.app.admin.service.SysLoginService,
    private val userService: SysUserService
) : AuthStrategy {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${user.password.maxRetryCount:5}")
    private val maxRetryCount: Int = 5

    @Value("\${user.password.lockTime:10}")
    private val lockTime: Int = 10

    override fun login(body: String, client: SysClientVo): LoginVo {
        val loginBody = JsonUtils.parseObject(body, PasswordLoginBody::class.java)
        ValidatorUtils.validate(loginBody)

        val tenantId = loginBody!!.tenantId ?: ""
        val username = loginBody.username ?: ""
        val password = loginBody.password ?: ""
        val code = loginBody.code
        val uuid = loginBody.uuid

        val captchaEnabled = captchaProperties.enable
        // 验证码开关
        if (captchaEnabled) {
            validateCaptcha(tenantId, username, code, uuid)
        }

        val loginUser = TenantHelper.dynamic(tenantId) {
            val user = loadUserByUsername(username)

            sysLoginService.checkLogin(LoginType.PASSWORD, tenantId, username) {
                !userService.validatePassword(username, password)
            }
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
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    private fun validateCaptcha(tenantId: String?, username: String?, code: String?, uuid: String?) {
        val verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.blankToDefault(uuid, "")
        val captcha = RedisUtils.getCacheObject<String>(verifyKey)
        RedisUtils.deleteObject(verifyKey)
        if (captcha == null) {
            sysLoginService.recordLogininfor(tenantId ?: "", username ?: "", Constants.LOGIN_FAIL,
                MessageUtils.message("user.jcaptcha.expire") ?: "验证码已过期")
            throw CaptchaExpireException()
        }
        if (!StringUtils.equalsIgnoreCase(code ?: "", captcha)) {
            sysLoginService.recordLogininfor(tenantId ?: "", username ?: "", Constants.LOGIN_FAIL,
                MessageUtils.message("user.jcaptcha.error") ?: "验证码错误")
            throw CaptchaException("user.jcaptcha.error")
        }
    }

    private fun loadUserByUsername(username: String): SysUserVo {
        // 使用 userService 查询用户
        val userVo = userService.selectUserByUserName(username)
        if (userVo == null) {
            log.info("登录用户：{} 不存在.", username)
            throw UserException("user.not.exists", username)
        } else if (SystemConstants.DISABLE == userVo.status) {
            log.info("登录用户：{} 已被停用.", username)
            throw UserException("user.blocked", username)
        }
        return userVo
    }
}
