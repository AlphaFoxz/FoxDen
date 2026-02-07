package com.github.alphafoxz.foxden.common.oss.constant

import com.github.alphafoxz.foxden.common.core.constant.GlobalConstants

/**
 * 对象存储常量
 */
object OssConstant {
    /**
     * 默认配置KEY
     */
    const val DEFAULT_CONFIG_KEY: String = GlobalConstants.GLOBAL_REDIS_KEY + "sys_oss:default_config"

    const val LOCAL = "local"

    /**
     * 预览列表资源开关Key
     */
    const val PEREVIEW_LIST_RESOURCE_KEY = "sys.oss.previewListResource"

    /**
     * 系统数据ids
     */
    val SYSTEM_DATA_IDS: List<Long> = listOf(1L, 2L, 3L, 4L)

    /**
     * 云服务商
     */
    val CLOUD_SERVICE: Array<String> = arrayOf("aliyun", "qcloud", "qiniu", "obs")

    /**
     * https 状态
     */
    const val IS_HTTPS = "Y"
}
