package com.github.alphafoxz.foxden.common.core.constant

/**
 * 缓存的key 常量
 */
object CacheConstants {
    /** 在线用户 redis key */
    const val ONLINE_TOKEN_KEY = "online_tokens:"

    /** 参数管理 cache key */
    const val SYS_CONFIG_KEY = "sys_config:"

    /** 字典管理 cache key */
    const val SYS_DICT_KEY = "sys_dict:"

    /** 登录账户密码错误次数 redis key */
    const val PWD_ERR_CNT_KEY = "pwd_err_cnt:"

    /** 公文处理 */
    const val HANDLE_OFFICIAL_DOC = "handle_official_doc:"
}
