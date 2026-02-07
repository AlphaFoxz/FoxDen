package com.github.alphafoxz.foxden.common.excel.annotation

/**
 * excel 列单元格合并(合并列相同项)
 *
 * 需搭配 [com.github.alphafoxz.foxden.common.excel.core.CellMergeStrategy] 策略使用
 *
 * @author Lion Li
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class CellMerge(
    /**
     * col index
     */
    val index: Int = -1,

    /**
     * 合并需要依赖的其他字段名称
     */
    val mergeBy: Array<String> = []
)
