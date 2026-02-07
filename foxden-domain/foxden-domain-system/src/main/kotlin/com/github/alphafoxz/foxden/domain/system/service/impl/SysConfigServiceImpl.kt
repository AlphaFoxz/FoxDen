package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysConfigService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.domain.system.bo.SysConfigBo
import com.github.alphafoxz.foxden.domain.system.vo.SysConfigVo

/**
 * Config 业务层处理
 */
@Service
class SysConfigServiceImpl(): SysConfigService {

    override fun selectConfigList(config: SysConfigBo): List<SysConfigVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectConfigByKey(configKey: String): String? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun selectRegisterEnabled(tenantId: String?): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun selectConfigObject(configKey: String): SysConfigVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun selectConfigValueByKey(configKey: String): String? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun selectConfigValueByKey(configKey: String, defaultValue: String): String {
    return ""
        // TODO: 实现业务逻辑
    }

    override fun insertConfig(bo: SysConfigBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateConfig(bo: SysConfigBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteConfigByIds(configIds: Array<Long>) {
        // TODO: 实现业务逻辑
    }

    override fun resetConfigCache() {
        // TODO: 实现业务逻辑
    }

    override fun checkConfigKeyUnique(config: SysConfigBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }
}
