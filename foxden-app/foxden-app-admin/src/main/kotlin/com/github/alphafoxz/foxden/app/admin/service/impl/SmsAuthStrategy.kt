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
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.exception.user.CaptchaException
import com.github.alphafoxz.foxden.common.core.exception.user.CaptchaExpireException
import com.github.alphafoxz.foxden.common.core.exception.user.UserException
import com.github.alphafoxz.foxden.common.core.utils.MessageUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.core.utils.ValidatorUtils
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.security.utils.tokenTimeout
import com.github.alphafoxz.foxden.common.security.utils.tokenValue
import com.github.alphafoxz.foxden.common.jimmer.helper.TenantHelper
import com.github.alphafoxz.foxden.app.admin.service.SysLoginService
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import com.github.alphafoxz.foxden.domain.system.vo.SysClientVo
import com.github.alphafoxz.foxden.domain.system.vo.SysUserVo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service("smsAuth")
class SmsAuthStrategy(
    private val userService: SysUserService,
    private val sysLoginService: SysLoginService,
    private val captchaProperties: com.github.alphafoxz.foxden.common.web.config.properties.CaptchaProperties
) : AuthStrategy {

    companion object {
        const val BASE_NAME = "sms"
    }

    private val log = LoggerFactory.getLogger(javaClass)

    override fun login(body: String, client: SysClientVo): LoginVo {
        val loginBody = JsonUtils.parseObject(body, SmsLoginBody::class.java)
        ValidatorUtils.validate(loginBody)

        val tenantId = loginBody!!.tenantId ?: ""
        val phonenumber = loginBody!!.phonenumber ?: ""
        val smsCode = loginBody!!.smsCode ?: ""
        val uuid = loginBody!!.uuid ?: ""

        val captchaEnabled = captchaProperties.enable
        // 验证码开关
        if (captchaEnabled) {
            validateSmsCode(tenantId, phonenumber, smsCode, uuid)
        }

        val loginUser = TenantHelper.dynamicTenant(tenantId) {
            val user = loadUserByPhonenumber(phonenumber)

            sysLoginService.checkLogin(LoginType.SMS, tenantId, phonenumber) {
                !validateSmsCode(tenantId, phonenumber, smsCode, uuid)
            }
            // 此处可根据登录用户的数据不同 自行创建 loginUser
            sysLoginService.buildLoginUser(user)
        }

        loginUser.clientKey = client.clientKey
        loginUser.deviceType = client.deviceType

        val model = SaLoginParameter()
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
     * 校验短信验证码
     *
     * @param tenantId 租户ID
     * @param phonenumber 手机号
     * @param smsCode 验证码
     * @param uuid 唯一标识
     */
    private fun validateSmsCode(tenantId: String?, phonenumber: String?, smsCode: String?, uuid: String?): Boolean {
        if (phonenumber.isNullOrBlank() || smsCode.isNullOrBlank()) {
            return false
        }

        val verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.blankToDefault(uuid, "")
        val captcha = RedisUtils.getCacheObject<String>(verifyKey)
        RedisUtils.deleteObject(verifyKey)

        if (captcha == null) {
            sysLoginService.recordLogininfor(tenantId ?: "", phonenumber ?: "", Constants.LOGIN_FAIL,
                MessageUtils.message("user.jcaptcha.expire") ?: "验证码已过期")
            throw CaptchaExpireException()
        }
        if (!StringUtils.equalsIgnoreCase(smsCode ?: "", captcha)) {
            sysLoginService.recordLogininfor(tenantId ?: "", phonenumber ?: "", Constants.LOGIN_FAIL,
                MessageUtils.message("user.jcaptcha.error") ?: "验证码错误")
            throw CaptchaException("user.jcaptcha.error")
        }
        return true
    }

    /**
     * 通过手机号加载用户
     *
     * @param phonenumber 手机号
     * @return 用户信息
     */
    private fun loadUserByPhonenumber(phonenumber: String): SysUserVo {
        // 使用 userService 查询用户
        val userVo = userService.selectUserByPhonenumber(phonenumber)
        if (userVo == null) {
            log.info("登录用户：{} 不存在.", phonenumber)
            throw UserException("user.not.exists", phonenumber)
        } else if (SystemConstants.DISABLE == userVo.status) {
            log.info("登录用户：{} 已被停用.", phonenumber)
            throw UserException("user.blocked", phonenumber)
        }
        return userVo
    }
}
