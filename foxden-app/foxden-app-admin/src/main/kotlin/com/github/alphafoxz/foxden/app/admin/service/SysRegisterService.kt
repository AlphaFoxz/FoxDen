package com.github.alphafoxz.foxden.app.admin.service

import cn.hutool.crypto.digest.BCrypt
import com.github.alphafoxz.foxden.common.core.constant.Constants
import com.github.alphafoxz.foxden.common.core.constant.GlobalConstants
import com.github.alphafoxz.foxden.common.core.domain.model.RegisterBody
import com.github.alphafoxz.foxden.common.core.enums.UserType
import com.github.alphafoxz.foxden.common.core.exception.user.CaptchaException
import com.github.alphafoxz.foxden.common.core.exception.user.CaptchaExpireException
import com.github.alphafoxz.foxden.common.core.exception.user.UserException
import com.github.alphafoxz.foxden.common.core.utils.MessageUtils
import com.github.alphafoxz.foxden.common.web.utils.ServletUtils
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.log.event.LogininforEvent
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.alphafoxz.foxden.common.jimmer.helper.TenantHelper
import com.github.alphafoxz.foxden.common.web.config.properties.CaptchaProperties
import com.github.alphafoxz.foxden.domain.system.bo.SysUserBo
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import org.springframework.stereotype.Service

/**
 * 注册校验方法
 *
 * @author FoxDen Team
 */
@Service
class SysRegisterService(
    private val userService: SysUserService,
    private val captchaProperties: CaptchaProperties
) {

    /**
     * 注册
     */
    fun register(registerBody: RegisterBody) {
        val tenantId = registerBody.tenantId ?: ""
        val username = registerBody.username ?: ""
        val password = registerBody.password ?: ""

        // 校验用户类型是否存在
        val userTypeVal = UserType.getUserType(registerBody.userType ?: UserType.SYS_USER.userType)
        val userType = userTypeVal.userType

        val captchaEnabled = captchaProperties.enable
        // 验证码开关
        if (captchaEnabled) {
            validateCaptcha(tenantId, username, registerBody.code, registerBody.uuid)
        }

        val sysUser = SysUserBo(
            userName = username,
            nickName = username,
            password = BCrypt.hashpw(password),
            userType = userType
        )

        val exist = TenantHelper.isEnable() && userService.checkUserNameUnique(SysUserBo(userName = username))

        if (exist) {
            throw UserException("user.register.save.error", username)
        }

        val regFlag = userService.registerUser(sysUser, tenantId)
        if (!regFlag) {
            throw UserException("user.register.error")
        }
        recordLogininfor(tenantId, username, Constants.REGISTER, MessageUtils.message("user.register.success") ?: "用户注册成功")
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
            recordLogininfor(tenantId ?: "", username ?: "", Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire") ?: "验证码已过期")
            throw CaptchaExpireException()
        }
        if (!StringUtils.equalsIgnoreCase(code ?: "", captcha)) {
            recordLogininfor(tenantId ?: "", username ?: "", Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error") ?: "验证码错误")
            throw CaptchaException("user.jcaptcha.error")
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
    private fun recordLogininfor(tenantId: String?, username: String?, status: String, message: String) {
        val logininforEvent = LogininforEvent().apply {
            this.tenantId = tenantId
            this.username = username
            this.status = status
            this.message = message
            this.request = ServletUtils.getRequest()
        }
        SpringUtils.context().publishEvent(logininforEvent)
    }
}
