package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.domain.system.bo.SysTenantPackageBo
import com.github.alphafoxz.foxden.domain.system.vo.SysTenantPackageVo

/**
 * 租户套餐 服务层
 *
 * @author Lion Li
 */
interface SysTenantPackageService {

    /**
     * 查询租户套餐列表
     *
     * @param bo 租户套餐信息
     * @return 租户套餐集合
     */
    fun selectTenantPackageList(bo: SysTenantPackageBo): List<SysTenantPackageVo>

    /**
     * 根据套餐ID查询租户套餐
     *
     * @param packageId 租户套餐ID
     * @return 租户套餐
     */
    fun selectTenantPackageById(packageId: Long): SysTenantPackageVo?

    /**
     * 校验租户套餐是否唯一
     *
     * @param bo 租户套餐信息
     * @return 结果
     */
    fun checkPackageNameUnique(bo: SysTenantPackageBo): Boolean

    /**
     * 新增租户套餐
     *
     * @param bo 租户套餐信息
     * @return 结果
     */
    fun insertTenantPackage(bo: SysTenantPackageBo): Int

    /**
     * 修改租户套餐
     *
     * @param bo 租户套餐信息
     * @return 结果
     */
    fun updateTenantPackage(bo: SysTenantPackageBo): Int

    /**
     * 删除租户套餐
     *
     * @param packageId 租户套餐ID
     */
    fun deleteTenantPackageById(packageId: Long)

    /**
     * 批量删除租户套餐
     *
     * @param packageIds 需要删除的租户套餐ID
     */
    fun deleteTenantPackageByIds(packageIds: Array<Long>)

    /**
     * 状态修改
     *
     * @param bo 租户套餐信息
     * @return 结果
     */
    fun changeStatus(bo: SysTenantPackageBo): Int
}
