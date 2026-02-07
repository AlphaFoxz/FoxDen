package com.github.alphafoxz.foxden.common.jimmer.annotation

/**
 * 数据权限注解，用于标记数据权限的占位符关键字和替换值
 * <p>
 * 一个注解只能对应一个模板
 * </p>
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DataColumn(
    /**
     * 数据权限模板的占位符关键字，默认为 "deptName"
     */
    val key: Array<String> = ["deptName"],

    /**
     * 数据权限模板的占位符替换值，默认为 "dept_id"
     */
    val value: Array<String> = ["dept_id"],

    /**
     * 权限标识符 用于通过菜单权限标识符来获取数据权限
     * 拥有此标识符的角色 将不会拼接此角色的数据过滤sql
     */
    val permission: String = ""
)
