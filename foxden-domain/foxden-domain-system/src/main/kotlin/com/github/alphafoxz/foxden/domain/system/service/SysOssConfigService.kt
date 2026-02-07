package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.domain.system.bo.SysOssConfigBo
import com.github.alphafoxz.foxden.domain.system.vo.SysOssConfigVo

/**
 * 对象存储配置 服务层
 *
 * @author Lion Li
 */
interface SysOssConfigService {

    /**
     * 查询对象存储配置列表
     *
     * @param bo 对象存储配置信息
     * @return 对象存储配置集合
     */
    fun selectOssConfigList(bo: SysOssConfigBo): List<SysOssConfigVo>

    /**
     * 根据OssConfigId查询对象存储配置
     *
     * @param ossConfigId 对象存储配置ID
     * @return 对象存储配置
     */
    fun selectOssConfigById(ossConfigId: Long): SysOssConfigVo?

    /**
     * 新增对象存储配置
     *
     * @param bo 对象存储配置信息
     * @return 结果
     */
    fun insertOssConfig(bo: SysOssConfigBo): Int

    /**
     * 修改对象存储配置
     *
     * @param bo 对象存储配置信息
     * @return 结果
     */
    fun updateOssConfig(bo: SysOssConfigBo): Int

    /**
     * 批量删除对象存储配置
     *
     * @param ossConfigIds 需要删除的对象存储配置ID
     */
    fun deleteOssConfigByIds(ossConfigIds: Array<Long>)

    /**
     * 校验对象存储配置是否唯一
     *
     * @param bo 对象存储配置信息
     * @return 结果
     */
    fun checkConfigKeyUnique(bo: SysOssConfigBo): Boolean
}
