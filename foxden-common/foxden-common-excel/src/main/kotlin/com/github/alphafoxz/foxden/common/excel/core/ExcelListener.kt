package com.github.alphafoxz.foxden.common.excel.core

import com.alibaba.excel.read.listener.ReadListener

/**
 * Excel 导入监听
 *
 * @author Lion Li
 */
interface ExcelListener<T> : ReadListener<T> {

    fun getExcelResult(): ExcelResult<T>
}
