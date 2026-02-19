package com.github.alphafoxz.foxden.app.admin.controller

import cn.dev33.satoken.annotation.SaIgnore
import cn.dev33.satoken.exception.NotLoginException
import cn.hutool.core.codec.Base64
import cn.hutool.core.collection.CollUtil
import cn.hutool.core.util.ObjectUtil
import com.github.alphafoxz.foxden.app.admin.domain.vo.LoginTenantVo
import com.github.alphafoxz.foxden.app.admin.domain.vo.LoginVo
import com.github.alphafoxz.foxden.app.admin.domain.vo.TenantListVo
import com.github.alphafoxz.foxden.app.admin.service.AuthStrategy
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.domain.model.LoginBody
import com.github.alphafoxz.foxden.common.core.domain.model.RegisterBody
import com.github.alphafoxz.foxden.common.core.domain.model.SocialLoginBody
import com.github.alphafoxz.foxden.common.core.utils.*
import com.github.alphafoxz.foxden.common.encrypt.annotation.ApiEncrypt
import com.github.alphafoxz.foxden.common.jimmer.helper.TenantHelper
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import com.github.alphafoxz.foxden.common.ratelimiter.annotation.RateLimiter
import com.github.alphafoxz.foxden.common.ratelimiter.enums.LimitType
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.social.config.properties.SocialProperties
import com.github.alphafoxz.foxden.common.social.utils.SocialUtils
import com.github.alphafoxz.foxden.common.sse.dto.SseMessageDto
import com.github.alphafoxz.foxden.common.sse.utils.SseMessageUtils
import com.github.alphafoxz.foxden.domain.system.bo.SysTenantBo
import com.github.alphafoxz.foxden.domain.system.service.SysConfigService
import com.github.alphafoxz.foxden.domain.system.service.SysTenantService
import com.github.alphafoxz.foxden.domain.system.service.extensions.queryList
import jakarta.servlet.http.HttpServletRequest
import me.zhyd.oauth.model.AuthResponse
import me.zhyd.oauth.model.AuthUser
import me.zhyd.oauth.utils.AuthStateUtils
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import cn.dev33.satoken.stp.StpUtil
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * 认证
 *
 * @author FoxDen Team
 */
@SaIgnore
@RestController
@RequestMapping("/auth")
class AuthController(
    private val loginService: com.github.alphafoxz.foxden.app.admin.service.SysLoginService,
    private val registerService: com.github.alphafoxz.foxden.app.admin.service.SysRegisterService,
    private val configService: SysConfigService,
    private val tenantService: SysTenantService,
    private val clientService: com.github.alphafoxz.foxden.domain.system.service.SysClientService,
    private val scheduledExecutorService: ScheduledExecutorService,
    private val socialProperties: SocialProperties,
    private val socialUserService: com.github.alphafoxz.foxden.domain.system.service.SysSocialService
) {
    private val log = LoggerFactory.getLogger(AuthController::class.java)

    /**
     * 登录方法
     *
     * @param body 登录信息
     * @return 结果
     */
    @ApiEncrypt
    @PostMapping("/login")
    fun login(@RequestBody body: String): R<LoginVo> {
        // 解析JSON获取clientId和grantType
        val loginBody = JsonUtils.parseObject(body, LoginBody::class.java)
        // 校验参数
        ValidatorUtils.validate(loginBody)

        val clientId = loginBody?.clientId ?: ""
        val grantType = loginBody?.grantType ?: ""

        val client = clientService.queryByClientId(clientId)
        // 查询不到 client 或 client 内不包含 grantType
        if (client == null || !StringUtils.contains(client.grantType, grantType)) {
            log.info("客户端id: {} 认证类型：{} 异常!.", clientId, grantType)
            return R.fail(MessageUtils.message("auth.grant.type.error") ?: "授权类型不正确")
        } else if (SystemConstants.NORMAL != client.status) {
            return R.fail(MessageUtils.message("auth.grant.type.blocked") ?: "授权类型已被禁用")
        }

        // 校验租户
        loginService.checkTenant(loginBody?.tenantId)

        // 登录 - 将原始JSON字符串传递给策略
        val loginVo = AuthStrategy.login(body, client, grantType)

        // 获取登录用户ID
        val userId = LoginHelper.getUserId()

        // 延迟5秒发送欢迎消息
        scheduledExecutorService.schedule({
            val dto = SseMessageDto()
            dto.message = "${DateUtils.getTodayHour(Date())}好，欢迎登录 FoxDen 后台管理系统"
            dto.userIds = listOf(userId!!)
            SseMessageUtils.publishMessage(dto)
        }, 5, TimeUnit.SECONDS)

        return R.ok(loginVo)
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    fun logout(): R<Void> {
        loginService.logout()
        return R.ok("退出成功")
    }

    /**
     * 用户注册
     */
    @ApiEncrypt
    @PostMapping("/register")
    fun register(@Validated @RequestBody user: RegisterBody): R<Void> {
        if (!configService.selectRegisterEnabled(user.tenantId)) {
            return R.fail("当前系统没有开启注册功能！")
        }
        registerService.register(user)
        return R.ok()
    }

    /**
     * 获取跳转URL
     *
     * @param source 登录来源
     * @param tenantId 租户ID
     * @param domain 域名
     * @return 结果
     */
    @GetMapping("/binding/{source}")
    fun authBinding(
        @PathVariable("source") source: String,
        @RequestParam tenantId: String,
        @RequestParam domain: String
    ): R<String> {
        val obj = socialProperties.type?.get(source)
        if (ObjectUtil.isNull(obj)) {
            return R.fail("$source 平台账号暂不支持")
        }
        val authRequest = SocialUtils.getAuthRequest(source, socialProperties)
        val map = mapOf(
            "tenantId" to tenantId,
            "domain" to domain,
            "state" to AuthStateUtils.createState()
        )
        val authorizeUrl = authRequest.authorize(
            Base64.encode(
                JsonUtils.toJsonString(map)?.toByteArray(StandardCharsets.UTF_8)
            )
        )
        return R.ok("操作成功", authorizeUrl)
    }

    /**
     * 前端回调绑定授权(需要token)
     *
     * @param loginBody 请求体
     * @return 结果
     */
    @PostMapping("/social/callback")
    fun socialCallback(@RequestBody loginBody: SocialLoginBody): R<Void> {
        // 校验token
        StpUtil.checkLogin()
        // 获取第三方登录信息
        val response = SocialUtils.loginAuth(
            loginBody.source!!,
            loginBody.socialCode!!,
            loginBody.socialState!!,
            socialProperties
        )
        val authUserData = response.data
        // 判断授权响应是否成功
        if (!response.ok()) {
            return R.fail(response.msg)
        }
        loginService.socialRegister(authUserData)
        return R.ok()
    }

    /**
     * 取消授权(需要token)
     *
     * @param socialId socialId
     * @return 结果
     */
    @DeleteMapping(value = ["/unlock/{socialId}"])
    fun unlockSocial(@PathVariable socialId: Long): R<Void> {
        // 校验token
        StpUtil.checkLogin()
        val rows = socialUserService.deleteWithValidById(socialId)
        return if (rows) R.ok() else R.fail("取消授权失败")
    }


    @RateLimiter(time = 60, count = 20, limitType = LimitType.IP)
    @GetMapping("/tenant/list")
    fun tenantList(request: HttpServletRequest): R<LoginTenantVo> {
        // 返回对象
        val result = LoginTenantVo()
        val enable = TenantHelper.isEnable()
        result.tenantEnabled = enable

        // 如果未开启租户这直接返回
        if (!enable) {
            return R.ok(result)
        }

        val tenantList = tenantService.queryList(SysTenantBo())
        val voList: List<TenantListVo> = tenantList.map { MapstructUtils.convert(it, TenantListVo::class.java)!! }

        try {
            // 如果只超管返回所有租户
            if (com.github.alphafoxz.foxden.common.security.utils.LoginHelper.isSuperAdmin()) {
                result.voList = voList
                return R.ok(result)
            }
        } catch (@Suppress("UNUSED_PARAMETER") ignored: NotLoginException) {
        }

        // 获取域名
        val host: String = request.getHeader("referer")?.let { referer ->
            // 这里从referer中取值是为了本地使用hosts添加虚拟域名，方便本地环境调试
            referer.split("//")[1].split("/")[0]
        } ?: run {
            URI(request.requestURL.toString()).host
        }

        // 根据域名进行筛选
        val list = StreamUtils.filter(voList) { vo ->
            StringUtils.equalsIgnoreCase(vo.domain, host)
        }
        result.voList = if (CollUtil.isNotEmpty(list)) list else voList

        return R.ok(result)
    }
}
