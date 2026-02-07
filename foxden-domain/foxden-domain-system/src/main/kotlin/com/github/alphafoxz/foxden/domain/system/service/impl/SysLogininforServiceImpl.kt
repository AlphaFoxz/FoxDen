package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysLogininforService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysLogininforBo
import com.github.alphafoxz.foxden.domain.system.vo.SysLogininforVo

/**
 * Logininfor 业务层处理
 */
@Service
class SysLogininforServiceImpl(): SysLogininforService {

    override fun selectPageList(bo: SysLogininforBo, pageQuery: PageQuery): TableDataInfo<SysLogininforVo> {
        // TODO: 实现业务逻辑
        return com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo.build(emptyList())
    }

    override fun insertLogininfor(bo: SysLogininforBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun selectLogininforById(infoId: Long): SysLogininforVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun deleteLogininforByIds(infoIds: Array<Long>) {
        // TODO: 实现业务逻辑
    }

    override fun cleanLogininfor() {
        // TODO: 实现业务逻辑
    }
}
