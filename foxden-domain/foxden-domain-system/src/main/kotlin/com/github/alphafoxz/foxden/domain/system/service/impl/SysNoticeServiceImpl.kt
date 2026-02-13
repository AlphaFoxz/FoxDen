package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysNoticeBo
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysNoticeService
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import com.github.alphafoxz.foxden.domain.system.service.extensions.saveWithAutoId
import com.github.alphafoxz.foxden.domain.system.vo.SysNoticeVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.like
import org.springframework.stereotype.Service

/**
 * Notice 业务层处理
 */
@Service
class SysNoticeServiceImpl(
    private val sqlClient: KSqlClient,
    private val userService: SysUserService
) : SysNoticeService {

    override fun selectNoticeList(notice: SysNoticeBo): List<SysNoticeVo> {
        // 预处理：如果有 createByName，先查询用户ID（可能为 null）
        val createByUserId = notice.createByName?.takeIf { it.isNotBlank() }?.let { userName ->
            userService.selectUserByUserName(userName)?.userId
        }

        val notices = sqlClient.createQuery(SysNotice::class) {
            // 公告标题模糊查询
            notice.noticeTitle?.takeIf { it.isNotBlank() }?.let {
                where(table.noticeTitle like "%${it}%")
            }
            // 公告类型精确匹配
            notice.noticeType?.takeIf { it.isNotBlank() }?.let {
                where(table.noticeType eq it)
            }
            // 创建人名称查询（通过用户名查询用户ID，然后匹配 createBy 字段）
            // 如果提供了 createByName，即使 userId 为 null 也要添加条件（让查询返回空结果）
            notice.createByName?.let {
                where(table.createBy eq createByUserId)
            }
            orderBy(table.id.asc())
            select(table)
        }.execute()

        return notices.map { entityToVo(it) }
    }

    override fun selectPageNoticeList(notice: SysNoticeBo, pageQuery: PageQuery): TableDataInfo<SysNoticeVo> {
        // 预处理：如果有 createByName，先查询用户ID（可能为 null）
        val createByUserId = notice.createByName?.takeIf { it.isNotBlank() }?.let { userName ->
            userService.selectUserByUserName(userName)?.userId
        }

        val pager = sqlClient.createQuery(SysNotice::class) {
            // 公告标题模糊查询
            notice.noticeTitle?.takeIf { it.isNotBlank() }?.let {
                where(table.noticeTitle like "%${it}%")
            }
            // 公告类型精确匹配
            notice.noticeType?.takeIf { it.isNotBlank() }?.let {
                where(table.noticeType eq it)
            }
            // 创建人名称查询（通过用户名查询用户ID，然后匹配 createBy 字段）
            // 如果提供了 createByName，即使 userId 为 null 也要添加条件（让查询返回空结果）
            notice.createByName?.let {
                where(table.createBy eq createByUserId)
            }
            orderBy(table.id.asc())
            select(table)
        }.fetchPage(pageQuery.getPageNumOrDefault() - 1, pageQuery.getPageSizeOrDefault())

        return TableDataInfo(pager.rows.map { entityToVo(it) }, pager.totalRowCount)
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

        val result = sqlClient.saveWithAutoId(newNotice)
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

    override fun deleteNoticeByIds(noticeIds: Array<Long>): Int {
        sqlClient.deleteByIds(SysNotice::class, noticeIds.toList())
        return noticeIds.size
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
