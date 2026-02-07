package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysOssConfigService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.domain.system.bo.SysOssConfigBo
import com.github.alphafoxz.foxden.domain.system.vo.SysOssConfigVo

/**
 * OssConfig 业务层处理
 */
@Service
class SysOssConfigServiceImpl(): SysOssConfigService {

    override fun selectOssConfigList(bo: SysOssConfigBo): List<SysOssConfigVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectOssConfigById(ossConfigId: Long): SysOssConfigVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun insertOssConfig(bo: SysOssConfigBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateOssConfig(bo: SysOssConfigBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteOssConfigByIds(ossConfigIds: Array<Long>) {
        // TODO: 实现业务逻辑
    }

    override fun checkConfigKeyUnique(bo: SysOssConfigBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }
}
