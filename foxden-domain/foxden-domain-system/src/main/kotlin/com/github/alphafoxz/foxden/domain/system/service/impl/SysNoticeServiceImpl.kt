package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysNoticeService
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.bo.SysNoticeBo
import com.github.alphafoxz.foxden.domain.system.vo.SysNoticeVo
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.*
import org.springframework.stereotype.Service

/**
 * Notice 业务层处理
 */
@Service
class SysNoticeServiceImpl(
    private val sqlClient: KSqlClient
) : SysNoticeService {

    override fun selectNoticeList(notice: SysNoticeBo): List<SysNoticeVo> {
        val notices = sqlClient.createQuery(SysNotice::class) {
            notice.noticeId?.let { where(table.id eq it) }
            notice.noticeTitle?.takeIf { it.isNotBlank() }?.let { where(table.noticeTitle like "%${it}%") }
            notice.noticeType?.takeIf { it.isNotBlank() }?.let { where(table.noticeType eq it) }
            notice.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            orderBy(table.id.asc())
            select(table)
        }.execute()

        return notices.map { entityToVo(it) }
    }

    override fun selectNoticeById(noticeId: Long): SysNoticeVo? {
        val notice = sqlClient.findById(SysNotice::class, noticeId) ?: return null
        return entityToVo(notice)
    }

    override fun insertNotice(bo: SysNoticeBo): Int {
        val newNotice = com.github.alphafoxz.foxden.domain.system.entity.SysNoticeDraft.`$`.produce {
            noticeTitle = bo.noticeTitle ?: throw ServiceException("公告标题不能为空")
            noticeType = bo.noticeType
            noticeContent = bo.noticeContent
            status = bo.status ?: SystemConstants.NORMAL
            remark = bo.remark
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(newNotice)
        return if (result.isModified) 1 else 0
    }

    override fun updateNotice(bo: SysNoticeBo): Int {
        val noticeIdVal = bo.noticeId ?: return 0

        val result = sqlClient.createUpdate(SysNotice::class) {
            where(table.id eq noticeIdVal)
            bo.noticeTitle?.let { set(table.noticeTitle, it) }
            bo.noticeType?.let { set(table.noticeType, it) }
            bo.noticeContent?.let { set(table.noticeContent, it) }
            bo.status?.let { set(table.status, it) }
            bo.remark?.let { set(table.remark, it) }
            set(table.updateTime, java.time.LocalDateTime.now())
        }.execute()
        return result
    }

    override fun deleteNoticeById(noticeId: Long) {
        sqlClient.deleteById(SysNotice::class, noticeId)
    }

    override fun deleteNoticeByIds(noticeIds: Array<Long>) {
        sqlClient.deleteByIds(SysNotice::class, noticeIds.toList())
    }

    /**
     * 实体转 VO
     */
    private fun entityToVo(notice: SysNotice): SysNoticeVo {
        return SysNoticeVo(
            noticeId = notice.id,
            noticeTitle = notice.noticeTitle,
            noticeType = notice.noticeType,
            noticeContent = notice.noticeContent,
            status = notice.status,
            remark = notice.remark,
            createTime = notice.createTime
            // Note: createBy in entity is Long (user ID), but VO expects String (username)
            // For now, skip setting this field
        )
    }
}
