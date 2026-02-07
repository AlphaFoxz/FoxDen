package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.domain.system.bo.SysConfigBo
import com.github.alphafoxz.foxden.domain.system.vo.SysConfigVo

/**
 * 参数配置 服务层
 *
 * @author Lion Li
 */
interface SysConfigService {

    /**
     * 查询参数配置信息
     *
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    fun selectConfigList(config: SysConfigBo): List<SysConfigVo>

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数配置信息
     */
    fun selectConfigByKey(configKey: String): String?

    /**
     * 获取注册开关
     *
     * @param tenantId 租户ID
     * @return 注册开关 true 开 false 关
     */
    fun selectRegisterEnabled(tenantId: String?): Boolean

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数配置信息
     */
    fun selectConfigObject(configKey: String): SysConfigVo?

    /**
     * 根据参数键名查询参数值
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    fun selectConfigValueByKey(configKey: String): String?

    /**
     * 根据参数键名查询参数值
     *
     * @param configKey   参数键名
     * @param defaultValue 默认值
     * @return 参数键值
     */
    fun selectConfigValueByKey(configKey: String, defaultValue: String): String

    /**
     * 新增参数配置
     *
     * @param bo 参数配置信息
     * @return 结果
     */
    fun insertConfig(bo: SysConfigBo): Int

    /**
     * 修改参数配置
     *
     * @param bo 参数配置信息
     * @return 结果
     */
    fun updateConfig(bo: SysConfigBo): Int

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    fun deleteConfigByIds(configIds: Array<Long>)

    /**
     * 重置参数缓存数据
     */
    fun resetConfigCache()

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    fun checkConfigKeyUnique(config: SysConfigBo): Boolean
}
