package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysClientService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.domain.system.bo.SysClientBo
import com.github.alphafoxz.foxden.domain.system.vo.SysClientVo

/**
 * Client 业务层处理
 */
@Service
class SysClientServiceImpl(): SysClientService {

    override fun selectClientList(bo: SysClientBo): List<SysClientVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectClientById(id: Long): SysClientVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun queryByClientId(clientId: String): SysClientVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun insertClient(bo: SysClientBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateClient(bo: SysClientBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun checkClientIdUnique(bo: SysClientBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun deleteClientById(id: Long) {
        // TODO: 实现业务逻辑
    }
}
