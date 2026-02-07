package com.github.alphafoxz.foxden.app.admin.controller

import cn.dev33.satoken.annotation.SaIgnore
import cn.hutool.core.util.IdUtil
import cn.hutool.core.util.RandomUtil
import com.github.alphafoxz.foxden.app.admin.domain.vo.CaptchaVo
import com.github.alphafoxz.foxden.common.core.constant.Constants
import com.github.alphafoxz.foxden.common.core.constant.GlobalConstants
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.reflect.ReflectUtils
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.alphafoxz.foxden.common.web.config.properties.CaptchaProperties
import com.github.alphafoxz.foxden.common.web.enums.CaptchaType
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import java.util.LinkedHashMap

/**
 * 验证码操作处理
 *
 * @author FoxDen Team
 */
@SaIgnore
@Validated
@RestController
class CaptchaController(
    private val captchaProperties: CaptchaProperties
) {

    /**
     * 生成验证码
     */
    @GetMapping("/auth/code")
    fun getCode(): R<CaptchaVo> {
        val captchaEnabled = captchaProperties.enable
        if (!captchaEnabled) {
            return R.ok(CaptchaVo(captchaEnabled = false))
        }
        return R.ok(SpringUtils.getAopProxy(this).getCodeImpl())
    }

    /**
     * 生成验证码
     * 独立方法避免验证码关闭之后仍然走限流
     */
    fun getCodeImpl(): CaptchaVo {
        // 保存验证码信息
        val uuid = IdUtil.simpleUUID()
        val verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + uuid
        // 生成验证码
        val captchaType = captchaProperties.type
        val codeGenerator = if (CaptchaType.MATH == captchaType) {
            ReflectUtils.newInstance(captchaType.clazz, captchaProperties.numberLength, false)
        } else {
            ReflectUtils.newInstance(captchaType.clazz, captchaProperties.charLength)
        }
        val captcha = SpringUtils.getBean(captchaProperties.category.clazz)
        captcha.setGenerator(codeGenerator)
        captcha.createCode()
        // 如果是数学验证码，使用SpEL表达式处理验证码结果
        var code = captcha.getCode()
        if (CaptchaType.MATH == captchaType) {
            val parser = org.springframework.expression.spel.standard.SpelExpressionParser()
            val exp = parser.parseExpression(com.github.alphafoxz.foxden.common.core.utils.StringUtils.remove(code, "="))
            code = exp.getValue(String::class.java)
        }
        RedisUtils.setCacheObject(verifyKey, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION.toLong()))
        return CaptchaVo(
            captchaEnabled = true,
            uuid = uuid,
            img = captcha.getImageBase64()
        )
    }
}
