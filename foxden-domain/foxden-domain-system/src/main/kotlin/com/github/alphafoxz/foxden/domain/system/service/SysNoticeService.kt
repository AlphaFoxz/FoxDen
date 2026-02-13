package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysNoticeBo
import com.github.alphafoxz.foxden.domain.system.vo.SysNoticeVo

/**
 * 公告 服务层
 *
 * @author Lion Li
 */
interface SysNoticeService {

    /**
     * 查询公告信息
     *
     * @param notice 公告信息
     * @return 公告信息集合
     */
    fun selectNoticeList(notice: SysNoticeBo): List<SysNoticeVo>

    /**
     * 分页查询公告信息
     *
     * @param notice 公告信息
     * @param pageQuery 分页参数
     * @return 分页公告信息
     */
    fun selectPageNoticeList(notice: SysNoticeBo, pageQuery: PageQuery): TableDataInfo<SysNoticeVo>

    /**
     * 根据公告ID查询信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    fun selectNoticeById(noticeId: Long): SysNoticeVo?

    /**
     * 新增公告
     *
     * @param bo 公告信息
     * @return 结果
     */
    fun insertNotice(bo: SysNoticeBo): Int

    /**
     * 修改公告
     *
     * @param bo 公告信息
     * @return 结果
     */
    fun updateNotice(bo: SysNoticeBo): Int

    /**
     * 删除公告信息
     *
     * @param noticeId 公告ID
     */
    fun deleteNoticeById(noticeId: Long)

    /**
     * 批量删除公告信息
     *
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    fun deleteNoticeByIds(noticeIds: Array<Long>): Int
}
