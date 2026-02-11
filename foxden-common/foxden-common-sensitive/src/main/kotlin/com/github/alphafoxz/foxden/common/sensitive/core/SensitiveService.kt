package com.github.alphafoxz.foxden.common.sensitive.core

/**
 * 脱敏服务
 * 默认管理员不过滤
 * 需自行根据业务重写实现
 *
 * @author Lion Li
 * @version 3.6.0
 */
interface SensitiveService {

    /**
     * 是否脱敏
     *
     * @param roleKey 角色标识符
     * @param perms 权限标识符
     * @return true 表示需要脱敏，false 表示不需要脱敏
     */
    fun isSensitive(roleKey: Array<String>, perms: Array<String>): Boolean
}
