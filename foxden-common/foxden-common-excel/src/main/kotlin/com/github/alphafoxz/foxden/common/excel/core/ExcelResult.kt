package com.github.alphafoxz.foxden.common.excel.core

/**
 * excel返回对象
 *
 * @author Lion Li
 */
interface ExcelResult<T> {

    /**
     * 对象列表
     */
    val list: List<T>

    /**
     * 错误列表
     */
    val errorList: List<String>

    /**
     * 导入回执
     */
    fun getAnalysis(): String
}
