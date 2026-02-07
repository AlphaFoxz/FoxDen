package com.github.alphafoxz.foxden.domain.system.service

/**
 * 数据权限 服务层
 *
 * @author Lion Li
 */
interface SysDataScopeService {

    /**
     * 根据角色ID查询部门ID列表
     *
     * @param roleId 角色ID
     * @return 部门ID列表
     */
    fun selectDeptIdsByRoleId(roleId: Long): List<Long>

    /**
     * 根据角色ID查询部门ID列表
     *
     * @param roleIds 角色ID
     * @return 部门ID列表
     */
    fun selectDeptIdsByRoleIds(roleIds: Array<Long>): List<Long>
}
