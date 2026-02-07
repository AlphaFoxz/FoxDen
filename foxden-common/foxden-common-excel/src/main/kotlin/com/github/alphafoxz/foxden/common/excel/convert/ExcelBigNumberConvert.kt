package com.github.alphafoxz.foxden.common.excel.convert

import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ObjectUtil
import com.alibaba.excel.converters.Converter
import com.alibaba.excel.enums.CellDataTypeEnum
import com.alibaba.excel.metadata.GlobalConfiguration
import com.alibaba.excel.metadata.data.ReadCellData
import com.alibaba.excel.metadata.data.WriteCellData
import com.alibaba.excel.metadata.property.ExcelContentProperty
import org.slf4j.LoggerFactory
import java.math.BigDecimal

/**
 * 大数值转换
 * Excel 数值长度位15位 大于15位的数值转换位字符串
 *
 * @author Lion Li
 */
class ExcelBigNumberConvert : Converter<Long> {
    private val log = LoggerFactory.getLogger(ExcelBigNumberConvert::class.java)

    override fun supportJavaTypeKey(): Class<Long> {
        return Long::class.java
    }

    override fun supportExcelTypeKey(): CellDataTypeEnum {
        return CellDataTypeEnum.STRING
    }

    override fun convertToJavaData(
        cellData: ReadCellData<*>,
        contentProperty: ExcelContentProperty?,
        globalConfiguration: GlobalConfiguration?
    ): Long {
        return Convert.toLong(cellData.data)
    }

    override fun convertToExcelData(
        `object`: Long?,
        contentProperty: ExcelContentProperty?,
        globalConfiguration: GlobalConfiguration?
    ): WriteCellData<*> {
        if (ObjectUtil.isNotNull(`object`)) {
            val str = Convert.toStr(`object`)
            if (str.length > 15) {
                return WriteCellData<Any>(str)
            }
        }
        val cellData = WriteCellData<Any>(BigDecimal.valueOf(`object`!!))
        cellData.type = CellDataTypeEnum.NUMBER
        return cellData
    }
}
