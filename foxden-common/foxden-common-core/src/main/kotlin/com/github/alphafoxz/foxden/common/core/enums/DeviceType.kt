package com.github.alphafoxz.foxden.common.core.enums

/**
 * 设备类型
 */
enum class DeviceType(val device: String) {
    /** pc端 */
    PC("pc"),

    /** app端 */
    APP("app"),

    /** 小程序端 */
    XCX("xcx"),

    /** 第三方社交登录平台 */
    SOCIAL("social")
}
