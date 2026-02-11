package com.github.alphafoxz.foxden.common.core.service

/**
 * 部门服务接口
 *
 * @author Lion Li
 */
interface DeptService {

    /**
     * 根据部门ID字符串获取对应的部门名称列表（逗号分隔）
     *
     * @param deptIds 以逗号分隔的部门ID字符串
     * @return 部门名称字符串（逗号分隔）
     */
    fun selectDeptNameByIds(deptIds: String): String?
}
