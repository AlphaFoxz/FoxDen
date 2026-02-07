package com.github.alphafoxz.foxden.common.core.constant

/**
 * 缓存组名称常量
 *
 * key 格式为 cacheNames#ttl#maxIdleTime#maxSize#local
 * ttl 过期时间 如果设置为0则不过期 默认为0
 * maxIdleTime 最大空闲时间 根据LRU算法清理空闲数据 如果设置为0则不检测 默认为0
 * maxSize 组最大长度 根据LRU算法清理溢出数据 如果设置为0则无限长 默认为0
 * local 默认开启本地缓存为1 关闭本地缓存为0
 *
 * 例子: test#60s、test#0#60s、test#0#1m#1000、test#1h#0#500、test#1h#0#500#0
 */
object CacheNames {
    /** 演示案例 */
    const val DEMO_CACHE = "demo:cache#60s#10m#20"

    /** 系统配置 */
    const val SYS_CONFIG = "sys_config"

    /** 数据字典 */
    const val SYS_DICT = "sys_dict"

    /** 数据字典类型 */
    const val SYS_DICT_TYPE = "sys_dict_type"

    /** 租户 */
    const val SYS_TENANT = "${GlobalConstants.GLOBAL_REDIS_KEY}sys_tenant#30d"

    /** 客户端 */
    const val SYS_CLIENT = "${GlobalConstants.GLOBAL_REDIS_KEY}sys_client#30d"

    /** 用户账户 */
    const val SYS_USER_NAME = "sys_user_name#30d"

    /** 用户名称 */
    const val SYS_NICKNAME = "sys_nickname#30d"

    /** 部门 */
    const val SYS_DEPT = "sys_dept#30d"

    /** OSS内容 */
    const val SYS_OSS = "sys_oss#30d"

    /** 角色自定义权限 */
    const val SYS_ROLE_CUSTOM = "sys_role_custom#30d"

    /** 部门及以下权限 */
    const val SYS_DEPT_AND_CHILD = "sys_dept_and_child#30d"

    /** 知识库企业全部 */
    const val KB_ENTERPRISE_ALL = "kb_enterprise_all#30d"

    /** OSS配置 */
    const val SYS_OSS_CONFIG = "${GlobalConstants.GLOBAL_REDIS_KEY}sys_oss_config"

    /** 在线用户 */
    const val ONLINE_TOKEN = "online_tokens"

    /**
     * 获取 Duix 指纹 key
     */
    fun duixFingerprintKey(fingerprint: String?): String {
        val fp = fingerprint ?: "unknown"
        return "duix_fingerprint:$fp"
    }

    /**
     * 获取 Duix 会话 key
     */
    fun duixSessionKey(sessionId: String?): String {
        val sid = sessionId ?: "unknown"
        return "duix_sessionid:$sid"
    }
}
