package com.github.alphafoxz.foxden.common.jimmer.enums

import com.github.alphafoxz.foxden.common.core.utils.StringUtils

/**
 * 数据权限类型枚举
 *
 * @author Lion Li
 * @version 3.5.0
 */
enum class DataScopeType(
    val code: String,
    val sqlTemplate: String,
    val elseSql: String
) {
    /**
     * 全部数据权限
     */
    ALL("1", "", ""),

    /**
     * 自定数据权限
     */
    CUSTOM("2", " #{#deptName} IN ( #{@sdss.getRoleCustom( #user.roleId )} ) ", " 1 = 0 "),

    /**
     * 部门数据权限
     */
    DEPT("3", " #{#deptName} = #{#user.deptId} ", " 1 = 0 "),

    /**
     * 部门及以下数据权限
     */
    DEPT_AND_CHILD("4", " #{#deptName} IN ( #{@sdss.getDeptAndChild( #user.deptId )} )", " 1 = 0 "),

    /**
     * 仅本人数据权限
     */
    SELF("5", " #{#userName} = #{#user.userId} ", " 1 = 0 "),

    /**
     * 部门及以下或本人数据权限
     */
    DEPT_AND_CHILD_OR_SELF("6", " #{#deptName} IN ( #{@sdss.getDeptAndChild( #user.deptId )} ) OR #{#userName} = #{#user.userId} ", " 1 = 0 ");

    companion object {
        /**
         * 根据枚举代码查找对应的枚举值
         */
        @JvmStatic
        fun findCode(code: String?): DataScopeType? {
            if (StringUtils.isBlank(code)) {
                return null
            }
            return values().firstOrNull { it.code == code }
        }
    }
}
