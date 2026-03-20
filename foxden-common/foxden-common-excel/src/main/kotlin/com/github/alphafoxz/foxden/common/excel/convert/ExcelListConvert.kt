package com.github.alphafoxz.foxden.common.excel.convert

import com.alibaba.excel.converters.Converter
import com.alibaba.excel.enums.CellDataTypeEnum
import com.alibaba.excel.metadata.GlobalConfiguration
import com.alibaba.excel.metadata.data.ReadCellData
import com.alibaba.excel.metadata.data.WriteCellData
import com.alibaba.excel.metadata.property.ExcelContentProperty

/**
 * Excel List 转换器
 *
 * 将 List 类型转换为逗号分隔的字符串用于导出
 *
 * @author FoxDen Team
 */
class ExcelListConvert : Converter<List<*>> {

    override fun supportJavaTypeKey(): Class<List<*>> {
        return List::class.java
    }

    override fun supportExcelTypeKey(): CellDataTypeEnum {
        return CellDataTypeEnum.STRING
    }

    override fun convertToExcelData(
        value: List<*>?,
        contentProperty: ExcelContentProperty?,
        globalConfiguration: GlobalConfiguration?
    ): WriteCellData<String> {
        if (value == null || value.isEmpty()) {
            return WriteCellData<String>("")
        }
        // 将 List 转换为逗号分隔的字符串
        return WriteCellData<String>(value.joinToString(","))
    }

    override fun convertToJavaData(
        cellData: ReadCellData<*>,
        contentProperty: ExcelContentProperty?,
        globalConfiguration: GlobalConfiguration?
    ): List<*>? {
        // 导入时不需要支持，返回空列表
        return emptyList<Any>()
    }
}
