package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysOperLogBo
import com.github.alphafoxz.foxden.domain.system.vo.SysOperLogVo

/**
 * 操作日志 服务层
 *
 * @author Lion Li
 */
interface SysOperLogService {

    /**
     * 分页查询操作日志
     *
     * @param bo       操作日志对象
     * @param pageQuery 分页参数
     * @return 操作日志集合
     */
    fun selectPageList(bo: SysOperLogBo, pageQuery: PageQuery): TableDataInfo<SysOperLogVo>

    /**
     * 新增操作日志
     *
     * @param bo 操作日志对象
     */
    fun insertOperlog(bo: SysOperLogBo)

    /**
     * 查询操作日志
     *
     * @param operId 日志ID
     * @return 操作日志
     */
    fun selectOperLogById(operId: Long): SysOperLogVo?

    /**
     * 批量删除操作日志
     *
     * @param operIds 需要删除的日志ID
     */
    fun deleteOperLogByIds(operIds: Array<Long>)

    /**
     * 清空操作日志
     */
    fun cleanOperLog()
}
