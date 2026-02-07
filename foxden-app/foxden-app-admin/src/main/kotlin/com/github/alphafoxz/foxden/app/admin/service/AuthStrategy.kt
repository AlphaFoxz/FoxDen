package com.github.alphafoxz.foxden.app.admin.service

import com.github.alphafoxz.foxden.app.admin.domain.vo.LoginVo
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.domain.system.vo.SysClientVo

/**
 * 授权策略
 *
 * @author FoxDen Team
 */
interface AuthStrategy {

    companion object {
        const val BASE_NAME = "AuthStrategy"

        /**
         * 登录
         *
         * @param body      登录对象
         * @param client    授权管理视图对象
         * @param grantType 授权类型
         * @return 登录验证信息
         */
        fun login(body: String, client: SysClientVo, grantType: String): LoginVo {
            // 授权类型和客户端id
            val beanName = grantType + BASE_NAME
            if (!SpringUtils.containsBean(beanName)) {
                throw ServiceException("授权类型不正确!")
            }
            val instance = SpringUtils.getBean<AuthStrategy>(beanName, AuthStrategy::class.java)
            return instance.login(body, client)
        }
    }

    /**
     * 登录
     *
     * @param body   登录对象
     * @param client 授权管理视图对象
     * @return 登录验证信息
     */
    fun login(body: String, client: SysClientVo): LoginVo
}
