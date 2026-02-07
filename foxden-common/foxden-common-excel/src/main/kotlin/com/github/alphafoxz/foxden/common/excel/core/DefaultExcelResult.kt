package com.github.alphafoxz.foxden.common.excel.core

import cn.hutool.core.util.StrUtil

/**
 * 默认excel返回对象
 *
 * @author Yjoioooo
 * @author Lion Li
 */
class DefaultExcelResult<T> : ExcelResult<T> {

    /**
     * 数据对象list
     */
    private val _list: MutableList<T> = mutableListOf()

    /**
     * 错误信息列表
     */
    private val _errorList: MutableList<String> = mutableListOf()

    // Override interface properties with backing fields
    override val list: List<T> get() = _list.toList()

    override val errorList: List<String> get() = _errorList.toList()

    // Internal access to mutable lists
    val mutableList: MutableList<T> get() = _list
    val mutableErrorList: MutableList<String> get() = _errorList

    constructor()

    constructor(list: List<T>, errorList: List<String>) {
        this._list.addAll(list)
        this._errorList.addAll(errorList)
    }

    constructor(excelResult: ExcelResult<T>) {
        this._list.addAll(excelResult.list)
        this._errorList.addAll(excelResult.errorList)
    }

    fun addError(error: String) {
        _errorList.add(error)
    }

    fun addData(data: T) {
        _list.add(data)
    }

    /**
     * 获取导入回执
     *
     * @return 导入回执
     */
    override fun getAnalysis(): String {
        val successCount = _list.size
        val errorCount = _errorList.size
        return if (successCount == 0) {
            "读取失败，未解析到数据"
        } else {
            if (errorCount == 0) {
                StrUtil.format("恭喜您，全部读取成功！共{}条", successCount)
            } else {
                ""
            }
        }
    }

    fun setList(list: List<T>) {
        _list.clear()
        _list.addAll(list)
    }

    fun setErrorList(errorList: List<String>) {
        _errorList.clear()
        _errorList.addAll(errorList)
    }
}
