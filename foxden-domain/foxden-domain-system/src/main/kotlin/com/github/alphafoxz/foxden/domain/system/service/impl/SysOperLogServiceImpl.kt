package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysOperLogService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysOperLogBo
import com.github.alphafoxz.foxden.domain.system.vo.SysOperLogVo

/**
 * OperLog 业务层处理
 */
@Service
class SysOperLogServiceImpl(): SysOperLogService {

    override fun selectPageList(bo: SysOperLogBo, pageQuery: PageQuery): TableDataInfo<SysOperLogVo> {
        // TODO: 实现业务逻辑
        return com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo.build(emptyList())
    }

    override fun insertOperlog(bo: SysOperLogBo) {
        // TODO: 实现业务逻辑
    }

    override fun selectOperLogById(operId: Long): SysOperLogVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun deleteOperLogByIds(operIds: Array<Long>) {
        // TODO: 实现业务逻辑
    }

    override fun cleanOperLog() {
        // TODO: 实现业务逻辑
    }
}
