package com.github.alphafoxz.foxden.app.admin.service.impl

import cn.dev33.satoken.stp.StpUtil
import cn.dev33.satoken.stp.parameter.SaLoginParameter
import com.github.alphafoxz.foxden.app.admin.domain.vo.LoginVo
import com.github.alphafoxz.foxden.app.admin.service.AuthStrategy
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.domain.model.XcxLoginBody
import com.github.alphafoxz.foxden.common.core.domain.model.XcxLoginUser
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.ValidatorUtils
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.security.utils.tokenValue
import com.github.alphafoxz.foxden.common.security.utils.tokenTimeout
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import com.github.alphafoxz.foxden.domain.system.vo.SysClientVo
import com.github.alphafoxz.foxden.domain.system.vo.SysUserVo
import me.zhyd.oauth.config.AuthConfig
import me.zhyd.oauth.model.AuthCallback
import me.zhyd.oauth.model.AuthResponse
import me.zhyd.oauth.model.AuthToken
import me.zhyd.oauth.model.AuthUser
import me.zhyd.oauth.request.AuthRequest
import me.zhyd.oauth.request.AuthWechatMiniProgramRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 小程序认证策略
 *
 * @author FoxDen Team
 */
@Service("xcx${AuthStrategy.BASE_NAME}")
class XcxAuthStrategy(
    private val userService: SysUserService
) : AuthStrategy {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun login(body: String, client: SysClientVo): LoginVo {
        val loginBody = JsonUtils.parseObject(body, XcxLoginBody::class.java)
        ValidatorUtils.validate(loginBody)

        // xcxCode 为 小程序调用 wx.login 授权后获取
        val xcxCode = loginBody!!.xcxCode
        // 多个小程序识别使用
        val appid = loginBody!!.appid

        // 校验 appid + appsrcret + xcxCode 调用登录凭证校验接口 获取 session_key 与 openid
        val authRequest: AuthRequest = AuthWechatMiniProgramRequest(
            AuthConfig.builder()
                .clientId(appid)
                .clientSecret("自行填写密钥 可根据不同appid填入不同密钥")
                .ignoreCheckRedirectUri(true)
                .ignoreCheckState(true)
                .build()
        )

        val authCallback = AuthCallback()
        authCallback.code = xcxCode

        val resp: AuthResponse<AuthUser> = authRequest.login(authCallback)

        val openid: String
        val unionId: String?

        if (resp.ok()) {
            val token: AuthToken = resp.data.token
            openid = token.openId
            // 微信小程序只有关联到微信开放平台下之后才能获取到 unionId，因此unionId不一定能返回。
            unionId = token.unionId
        } else {
            throw ServiceException(resp.msg)
        }

        // 框架登录不限制从什么表查询 只要最终构建出 LoginUser 即可
        val user = loadUserByOpenid(openid)

        // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
        val loginUser = XcxLoginUser().apply {
            tenantId = user.tenantId
            this.userId = user.userId
            username = user.userName
            nickname = user.nickName
            userType = user.userType
            clientKey = client.clientKey
            deviceType = client.deviceType
            this.openid = openid
        }

        val model = SaLoginParameter()
        model.deviceType = client.deviceType
        model.setTimeout(client.timeout ?: -1L)
        model.setActiveTimeout(client.activeTimeout ?: -1L)
        model.setExtra(LoginHelper.CLIENT_KEY, client.clientId)

        LoginHelper.login(loginUser, model)

        return LoginVo(
            accessToken = tokenValue(),
            expireIn = tokenTimeout(),
            clientId = client.clientId,
            openid = openid
        )
    }

    private fun loadUserByOpenid(openid: String): SysUserVo {
        // 使用 openid 查询绑定用户 如未绑定用户 则根据业务自行处理 例如 创建默认用户
        // TODO: 自行实现 userService.selectUserByOpenid(openid);
        val user = SysUserVo() // 临时占位，需要根据实际业务实现

        if (user.userId == null) {
            log.info("登录用户：{} 不存在.", openid)
            // TODO: 用户不存在 业务逻辑自行实现
        } else if (SystemConstants.DISABLE == user.status) {
            log.info("登录用户：{} 已被停用.", openid)
            // TODO: 用户已被停用 业务逻辑自行实现
        }

        return user
    }
}
