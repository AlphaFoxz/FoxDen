package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysSocialService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.domain.system.bo.SysSocialBo
import com.github.alphafoxz.foxden.domain.system.vo.SysSocialVo

/**
 * Social 业务层处理
 */
@Service
class SysSocialServiceImpl(): SysSocialService {

    override fun selectSocialList(bo: SysSocialBo): List<SysSocialVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectSocialById(socialId: Long): SysSocialVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun selectByAuthId(authId: String): List<SysSocialVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun insertSocial(bo: SysSocialBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateSocial(bo: SysSocialBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteSocialById(socialId: Long) {
        // TODO: 实现业务逻辑
    }
}
