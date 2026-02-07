package com.github.alphafoxz.foxden.common.security.utils

import cn.dev33.satoken.stp.StpUtil
import cn.dev33.satoken.stp.parameter.SaLoginParameter

/**
 * SaToken 扩展函数和属性
 */

/**
 * SaLoginParameter 扩展属性 - 设置设备类型
 */
var SaLoginParameter.deviceType: String?
    get() = getExtra("deviceType") as? String
    set(value) {
        setExtra("deviceType", value)
    }

/**
 * 获取当前 Token 值
 * 使用 StpUtil.getTokenValue() 的扩展函数形式
 */
fun tokenValue(): String = StpUtil.getTokenValue()

/**
 * 获取 Token 超时时间（秒）
 * 使用 StpUtil.getTokenTimeout() 的扩展函数形式
 */
fun tokenTimeout(): Long = StpUtil.getTokenTimeout()
