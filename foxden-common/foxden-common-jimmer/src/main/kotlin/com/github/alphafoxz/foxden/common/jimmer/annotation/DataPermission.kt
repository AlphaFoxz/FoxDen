package com.github.alphafoxz.foxden.common.jimmer.annotation

/**
 * 数据权限组注解，用于标记数据权限配置数组
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DataPermission(
    /**
     * 数据权限配置数组，用于指定数据权限的占位符关键字和替换值
     */
    val value: Array<DataColumn>,

    /**
     * 权限拼接标识符(用于指定连接语句的sql符号)
     * 如不填 默认 select 用 OR 其他语句用 AND
     * 内容 OR 或者 AND
     */
    val joinStr: String = ""
)
