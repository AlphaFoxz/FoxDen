package com.github.alphafoxz.foxden.common.excel.annotation

/**
 * Excel 批注
 *
 * @author guzhouyanyu
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExcelNotation(
    /**
     * 批注内容
     */
    val value: String
)
