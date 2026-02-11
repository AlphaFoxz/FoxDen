package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.domain.system.bo.SysTenantBo
import com.github.alphafoxz.foxden.domain.system.vo.SysTenantVo

/**
 * 租户服务
 *
 * @author Lion Li
 */
interface SysTenantService {

    /**
     * 查询租户列表
     *
     * @param bo 租户信息
     * @return 租户集合
     */
    fun selectTenantList(bo: SysTenantBo): List<SysTenantVo>

    /**
     * 根据租户ID查询租户信息
     *
     * @param tenantId 租户ID
     * @return 租户信息
     */
    fun queryByTenantId(tenantId: String): SysTenantVo?

    /**
     * 校验租户是否允许操作
     *
     * @param tenantId 租户ID
     */
    fun checkTenantAllowed(tenantId: String)

    /**
     * 校验租户名称是否唯一
     *
     * @param bo 租户信息
     * @return 结果
     */
    fun checkTenantNameUnique(bo: SysTenantBo): Boolean

    /**
     * 校验用户名称是否唯一
     *
     * @param bo 租户信息
     * @return 结果
     */
    fun checkTenantUserNameUnique(bo: SysTenantBo): Boolean

    /**
     * 新增租户
     *
     * @param bo 租户信息
     * @return 结果
     */
    fun insertTenant(bo: SysTenantBo): Int

    /**
     * 修改租户
     *
     * @param bo 租户信息
     * @return 结果
     */
    fun updateTenant(bo: SysTenantBo): Int

    /**
     * 异步修改租户状态
     *
     * @param tenantId 租户ID
     * @param status 租户状态
     */
    fun updateTenantStatus(tenantId: String, status: String)

    /**
     * 删除租户
     *
     * @param tenantId 租户ID
     */
    fun deleteTenantById(tenantId: String)

    /**
     * 同步租户套餐
     *
     * @param tenantIds 租户ID集合
     */
    fun syncTenantPackage(tenantIds: Array<String>)

    /**
     * 同步租户字典
     * 将默认租户的字典同步到所有租户
     */
    fun syncTenantDict()

    /**
     * 同步租户配置
     * 将默认租户的配置同步到所有租户
     */
    fun syncTenantConfig()
}
