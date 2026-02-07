package com.github.alphafoxz.foxden.common.excel.annotation

import org.apache.poi.ss.usermodel.IndexedColors

/**
 * 是否必填 此注解仅用于单表头 不支持多层级表头
 * @author guzhouyanyu
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExcelRequired(
    /**
     * 字体颜色
     */
    val fontColor: IndexedColors = IndexedColors.RED
)
