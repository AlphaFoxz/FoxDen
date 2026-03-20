package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysClientBo
import com.github.alphafoxz.foxden.domain.system.vo.SysClientVo

/**
 * 客户端管理 服务层
 *
 * @author Lion Li
 */
interface SysClientService {

    /**
     * 查询客户端管理
     *
     * @param bo 客户端管理信息
     * @return 客户端管理
     */
    fun selectClientList(bo: SysClientBo, pageQuery: PageQuery): TableDataInfo<SysClientVo>

    /**
     * 查询客户端管理列表（不分页，用于导出）
     *
     * @param bo 客户端管理信息
     * @return 客户端管理列表
     */
    fun selectClientList(bo: SysClientBo): List<SysClientVo>

    /**
     * 查询客户端管理详情
     *
     * @param id 客户端管理主键
     * @return 客户端管理
     */
    fun selectClientById(id: Long): SysClientVo?

    /**
     * 根据客户端ID查询详情
     *
     * @param clientId 客户端ID
     * @return 客户端管理
     */
    fun queryByClientId(clientId: String): SysClientVo?

    /**
     * 新增客户端管理
     *
     * @param bo 客户端管理信息
     * @return 结果
     */
    fun insertClient(bo: SysClientBo): Int

    /**
     * 修改客户端管理
     *
     * @param bo 客户端管理信息
     * @return 结果
     */
    fun updateClient(bo: SysClientBo): Int

    /**
     * 修改客户端状态
     *
     * @param clientId 客户端ID
     * @param status 状态
     * @return 结果
     */
    fun updateClientStatus(clientId: String, status: String): Int

    /**
     * 校验客户端key是否唯一
     *
     * @param bo 客户端管理信息
     * @return 结果
     */
    fun checkClientKeyUnique(bo: SysClientBo): Boolean

    /**
     * 删除客户端管理
     *
     * @param id 客户端管理主键
     */
    fun deleteClientById(id: Long)
}
