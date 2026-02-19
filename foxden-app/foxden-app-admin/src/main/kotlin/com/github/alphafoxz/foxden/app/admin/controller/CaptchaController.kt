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
import com.github.alphafoxz.foxden.common.mail.config.properties.MailProperties
import com.github.alphafoxz.foxden.common.mail.utils.MailUtils
import com.github.alphafoxz.foxden.common.ratelimiter.annotation.RateLimiter
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.alphafoxz.foxden.common.web.config.properties.CaptchaProperties
import com.github.alphafoxz.foxden.common.web.enums.CaptchaType
import jakarta.validation.constraints.NotBlank
import org.dromara.sms4j.api.SmsBlend
import org.dromara.sms4j.api.entity.SmsResponse
import org.dromara.sms4j.core.factory.SmsFactory
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

/**
 * 验证码操作处理
 *
 * @author FoxDen Team
 */
@SaIgnore
@Validated
@RestController
class CaptchaController(
    private val captchaProperties: CaptchaProperties,
    private val mailProperties: MailProperties
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 短信验证码
     *
     * @param phonenumber 用户手机号
     */
    @RateLimiter(key = "#phonenumber", time = 60, count = 1)
    @GetMapping("/resource/sms/code")
    fun smsCode(@NotBlank(message = "用户手机号不能为空") phonenumber: String): R<Void> {
        val key = GlobalConstants.CAPTCHA_CODE_KEY + phonenumber
        val code = RandomUtil.randomNumbers(4)
        RedisUtils.setCacheObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION.toLong()))
        // 验证码模板id 自行处理 (查数据库或写死均可)
        val templateId = ""
        val map = linkedMapOf("code" to code)
        val smsBlend: SmsBlend = SmsFactory.getSmsBlend("config1")
        val smsResponse: SmsResponse = smsBlend.sendMessage(phonenumber, templateId, map)
        if (!smsResponse.isSuccess) {
            log.error("验证码短信发送异常 => {}", smsResponse)
            return R.fail(smsResponse.data.toString())
        }
        return R.ok()
    }

    /**
     * 邮箱验证码
     *
     * @param email 邮箱
     */
    @GetMapping("/resource/email/code")
    fun emailCode(@NotBlank(message = "邮箱不能为空") email: String): R<Void> {
        if (!mailProperties.enabled) {
            return R.fail("当前系统没有开启邮箱功能！")
        }
        SpringUtils.getAopProxy(this).emailCodeImpl(email)
        return R.ok()
    }

    /**
     * 邮箱验证码
     * 独立方法避免验证码关闭之后仍然走限流
     */
    @RateLimiter(key = "#email", time = 60, count = 1)
    fun emailCodeImpl(email: String) {
        val key = GlobalConstants.CAPTCHA_CODE_KEY + email
        val code = RandomUtil.randomNumbers(4)
        RedisUtils.setCacheObject(key, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION.toLong()))
        try {
            MailUtils.sendText(
                email,
                "登录验证码",
                "您本次验证码为：" + code + "，有效性为" + Constants.CAPTCHA_EXPIRATION + "分钟，请尽快填写。"
            )
        } catch (e: Exception) {
            log.error("验证码邮件发送异常 => {}", e.message)
            throw ServiceException(e.message)
        }
    }

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
    @RateLimiter(time = 60, count = 10, limitType = com.github.alphafoxz.foxden.common.ratelimiter.enums.LimitType.IP)
    fun getCodeImpl(): CaptchaVo {
        // 保存验证码信息
        val uuid = IdUtil.simpleUUID()
        val verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + uuid
        // 生成验证码
        val captchaType = captchaProperties.type
        val codeGenerator = if (CaptchaType.MATH == captchaType) {
            // MathGenerator: (numberLength: Int)
            cn.hutool.captcha.generator.MathGenerator(captchaProperties.numberLength, false)
        } else {
            // 其他类型使用 CaptchaType 自带的创建方法
            captchaType.newInstance()
        }
        val captcha = SpringUtils.getBean(captchaProperties.category.clazz)
        captcha.setGenerator(codeGenerator)
        captcha.createCode()
        // 如果是数学验证码，使用SpEL表达式处理验证码结果
        var code = captcha.getCode()
        if (CaptchaType.MATH == captchaType) {
            val parser = org.springframework.expression.spel.standard.SpelExpressionParser()
            val exp =
                parser.parseExpression(com.github.alphafoxz.foxden.common.core.utils.StringUtils.remove(code, "="))
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
