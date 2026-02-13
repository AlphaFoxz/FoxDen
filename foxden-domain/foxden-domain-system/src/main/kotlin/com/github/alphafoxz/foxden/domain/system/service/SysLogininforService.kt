package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysLogininforBo
import com.github.alphafoxz.foxden.domain.system.vo.SysLogininforVo

/**
 * 登录日志 服务层
 *
 * @author Lion Li
 */
interface SysLogininforService {

    /**
     * 分页查询登录日志
     *
     * @param bo       登录日志信息
     * @param pageQuery 分页参数
     * @return 登录日志集合
     */
    fun selectPageLogininforList(bo: SysLogininforBo, pageQuery: PageQuery): TableDataInfo<SysLogininforVo>

    /**
     * 新增登录日志
     *
     * @param bo 登录日志信息
     * @return 结果
     */
    fun insertLogininfor(bo: SysLogininforBo): Int

    /**
     * 查询登录日志
     *
     * @param infoId 登录ID
     * @return 登录日志
     */
    fun selectLogininforById(infoId: Long): SysLogininforVo?

    /**
     * 批量删除登录日志
     *
     * @param infoIds 需要删除的登录ID
     */
    fun deleteLogininforByIds(infoIds: Array<Long>)

    /**
     * 查询登录日志集合（用于导出）
     *
     * @param bo 登录日志信息
     * @return 登录记录集合
     */
    fun selectList(bo: SysLogininforBo): List<SysLogininforVo>

    /**
     * 清空登录日志
     */
    fun cleanLogininfor()
}
