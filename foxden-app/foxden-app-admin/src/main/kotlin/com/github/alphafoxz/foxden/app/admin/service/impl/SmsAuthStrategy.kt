package com.github.alphafoxz.foxden.app.admin.service.impl

import cn.dev33.satoken.stp.StpUtil
import cn.dev33.satoken.stp.parameter.SaLoginParameter
import com.github.alphafoxz.foxden.app.admin.domain.vo.LoginVo
import com.github.alphafoxz.foxden.app.admin.service.AuthStrategy
import com.github.alphafoxz.foxden.common.core.constant.Constants
import com.github.alphafoxz.foxden.common.core.constant.GlobalConstants
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.domain.model.LoginUser
import com.github.alphafoxz.foxden.common.core.domain.model.SmsLoginBody
import com.github.alphafoxz.foxden.common.core.enums.LoginType
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
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import com.github.alphafoxz.foxden.domain.system.vo.SysClientVo
import com.github.alphafoxz.foxden.domain.system.vo.SysUserVo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 短信认证策略
 *
 * @author FoxDen Team
 */
@Service("sms${AuthStrategy.BASE_NAME}")
class SmsAuthStrategy(
    private val sysLoginService: com.github.alphafoxz.foxden.app.admin.service.SysLoginService,
    private val userService: SysUserService
) : AuthStrategy {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun login(body: String, client: SysClientVo): LoginVo {
        val loginBody = JsonUtils.parseObject(body, SmsLoginBody::class.java)
        ValidatorUtils.validate(loginBody)

        val tenantId = loginBody!!.tenantId ?: ""
        val phonenumber = loginBody!!.phonenumber ?: ""
        val smsCode = loginBody!!.smsCode ?: ""

        val loginUser = TenantHelper.dynamic(tenantId) {
            val user = loadUserByPhonenumber(phonenumber)
            sysLoginService.checkLogin(LoginType.SMS, tenantId, user.userName ?: "") {
                !validateSmsCode(tenantId, phonenumber, smsCode)
            }
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

    /**
     * 校验短信验证码
     */
    private fun validateSmsCode(tenantId: String?, phonenumber: String?, smsCode: String?): Boolean {
        val code = RedisUtils.getCacheObject<String>(GlobalConstants.CAPTCHA_CODE_KEY + phonenumber)
        if (StringUtils.isBlank(code)) {
            sysLoginService.recordLogininfor(tenantId ?: "", phonenumber ?: "", Constants.LOGIN_FAIL,
                MessageUtils.message("user.jcaptcha.expire") ?: "验证码已过期")
            throw CaptchaExpireException()
        }
        return code == smsCode
    }

    private fun loadUserByPhonenumber(phonenumber: String): SysUserVo {
        val user = userService.selectUserByPhonenumber(phonenumber)
        if (user == null) {
            log.info("登录用户：{} 不存在.", phonenumber)
            throw UserException("user.not.exists", phonenumber)
        } else if (SystemConstants.DISABLE == user.status) {
            log.info("登录用户：{} 已被停用.", phonenumber)
            throw UserException("user.blocked", phonenumber)
        }
        return user
    }
}
