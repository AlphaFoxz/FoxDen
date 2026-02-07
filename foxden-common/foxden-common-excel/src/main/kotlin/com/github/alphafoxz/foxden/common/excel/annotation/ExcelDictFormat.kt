package com.github.alphafoxz.foxden.common.excel.annotation

import com.github.alphafoxz.foxden.common.core.constant.Constants

/**
 * 字典格式化
 *
 * @author Lion Li
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExcelDictFormat(
    /**
     * 如果是字典类型，请设置字典的type值 (如: sys_user_sex)
     */
    val dictType: String = "",

    /**
     * 读取内容转表达式 (如: 0=男,1=女,2=未知)
     */
    val readConverterExp: String = "",

    /**
     * 分隔符，读取字符串组内容
     */
    val separator: String = Constants.SEPARATOR
)
