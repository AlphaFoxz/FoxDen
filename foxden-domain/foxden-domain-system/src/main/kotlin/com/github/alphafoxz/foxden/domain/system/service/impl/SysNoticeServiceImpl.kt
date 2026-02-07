package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysNoticeService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.domain.system.bo.SysNoticeBo
import com.github.alphafoxz.foxden.domain.system.vo.SysNoticeVo

/**
 * Notice 业务层处理
 */
@Service
class SysNoticeServiceImpl(): SysNoticeService {

    override fun selectNoticeList(notice: SysNoticeBo): List<SysNoticeVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectNoticeById(noticeId: Long): SysNoticeVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun insertNotice(bo: SysNoticeBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateNotice(bo: SysNoticeBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteNoticeById(noticeId: Long) {
        // TODO: 实现业务逻辑
    }

    override fun deleteNoticeByIds(noticeIds: Array<Long>) {
        // TODO: 实现业务逻辑
    }
}
