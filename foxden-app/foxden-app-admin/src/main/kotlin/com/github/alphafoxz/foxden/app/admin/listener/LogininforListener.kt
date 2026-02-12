package com.github.alphafoxz.foxden.app.admin.listener

import cn.hutool.http.useragent.UserAgentUtil
import com.github.alphafoxz.foxden.common.core.constant.Constants
import com.github.alphafoxz.foxden.common.core.utils.AddressUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.log.event.LogininforEvent
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.web.utils.ServletUtils
import com.github.alphafoxz.foxden.domain.system.bo.SysLogininforBo
import com.github.alphafoxz.foxden.domain.system.service.SysLogininforService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

/**
 * 登录日志事件监听器
 *
 * 处理登录/注销等事件，记录到数据库
 *
 * @author Lion Li
 */
@Component
class LogininforListener(
    private val logininforService: SysLogininforService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 记录登录信息
     */
    @Async
    @EventListener
    fun recordLogininfor(logininforEvent: LogininforEvent) {
        val request = logininforEvent.request
        val userAgent = UserAgentUtil.parse(request?.getHeader("User-Agent"))
        val ip = ServletUtils.getClientIP()

        // 客户端信息
        val clientId = request?.getHeader(LoginHelper.CLIENT_KEY)
        var client: com.github.alphafoxz.foxden.domain.system.vo.SysClientVo? = null
        if (StringUtils.isNotBlank(clientId)) {
            // TODO: 从 SysClientService 查询客户端信息
            // client = clientService.queryByClientId(clientId)
        }

        val address = AddressUtils.getRealAddressByIP(ip)

        // 构建日志信息
        val sb = StringBuilder()
        sb.append(getBlock(ip))
        sb.append(address)
        sb.append(getBlock(logininforEvent.username))
        sb.append(getBlock(logininforEvent.status))
        sb.append(getBlock(logininforEvent.message))

        // 打印日志
        log.info(sb.toString(), logininforEvent.args)

        // 获取客户端操作系统
        val osName = userAgent.os.name
        // 获取客户端浏览器
        val browserName = userAgent.browser.name

        // 封装对象
        val bo = SysLogininforBo().apply {
            tenantId = logininforEvent.tenantId
            userName = logininforEvent.username
            clientKey = client?.clientKey
            deviceType = client?.deviceType
            ipaddr = ip
            loginLocation = address
            browser = browserName
            os = osName
            msg = logininforEvent.message

            // 日志状态
            status = when (logininforEvent.status) {
                Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER -> Constants.SUCCESS
                Constants.LOGIN_FAIL -> Constants.FAIL
                else -> Constants.SUCCESS
            }
        }

        // 插入数据
        logininforService.insertLogininfor(bo)
    }

    /**
     * 获取信息块
     */
    private fun getBlock(msg: Any?): String {
        return if (msg == null) "" else "[$msg]"
    }
}
