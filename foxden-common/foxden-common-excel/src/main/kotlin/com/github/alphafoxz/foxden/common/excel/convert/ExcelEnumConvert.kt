package com.github.alphafoxz.foxden.common.excel.convert

import cn.hutool.core.annotation.AnnotationUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ObjectUtil
import com.alibaba.excel.converters.Converter
import com.alibaba.excel.enums.CellDataTypeEnum
import com.alibaba.excel.metadata.GlobalConfiguration
import com.alibaba.excel.metadata.data.ReadCellData
import com.alibaba.excel.metadata.data.WriteCellData
import com.alibaba.excel.metadata.property.ExcelContentProperty
import cn.hutool.core.util.ReflectUtil
import com.github.alphafoxz.foxden.common.excel.annotation.ExcelEnumFormat
import org.slf4j.LoggerFactory
import java.lang.reflect.Field

/**
 * 枚举格式化转换处理
 *
 * @author Liang
 */
class ExcelEnumConvert : Converter<Any?> {
    private val log = LoggerFactory.getLogger(ExcelEnumConvert::class.java)

    override fun supportJavaTypeKey(): Class<Any?> {
        return Any::class.java as Class<Any?>
    }

    override fun supportExcelTypeKey(): CellDataTypeEnum? {
        return null
    }

    override fun convertToJavaData(
        cellData: ReadCellData<*>,
        contentProperty: ExcelContentProperty?,
        globalConfiguration: GlobalConfiguration?
    ): Any? {
        // Excel中填入的是枚举中指定的描述
        val textValue: Any? = when (cellData.type) {
            CellDataTypeEnum.STRING, CellDataTypeEnum.DIRECT_STRING, CellDataTypeEnum.RICH_TEXT_STRING ->
                cellData.stringValue
            CellDataTypeEnum.NUMBER -> cellData.numberValue
            CellDataTypeEnum.BOOLEAN -> cellData.booleanValue
            else -> throw IllegalArgumentException("单元格类型异常!")
        }
        // 如果是空值
        if (ObjectUtil.isNull(textValue)) {
            return null
        }
        val enumCodeToTextMap = beforeConvert(contentProperty)
        // 从Java输出至Excel是code转text
        // 因此从Excel转Java应该将text与code对调
        val enumTextToCodeMap = enumCodeToTextMap.entries.associate { it.value to it.key }
        // 应该从text -> code中查找
        val codeValue = enumTextToCodeMap[textValue]
        return Convert.convert<Any?>(contentProperty?.field?.type, codeValue)
    }

    override fun convertToExcelData(
        `object`: Any?,
        contentProperty: ExcelContentProperty?,
        globalConfiguration: GlobalConfiguration?
    ): WriteCellData<String> {
        if (ObjectUtil.isNull(`object`)) {
            return WriteCellData("")
        }
        val enumValueMap = beforeConvert(contentProperty)
        val value = Convert.toStr(enumValueMap[`object`], "")
        return WriteCellData(value)
    }

    private fun beforeConvert(contentProperty: ExcelContentProperty?): Map<Any, String> {
        val anno = getAnnotation(contentProperty?.field ?: return emptyMap())
        val enumValueMap = mutableMapOf<Any, String>()
        @Suppress("UNCHECKED_CAST")
        val enumConstants = anno.enumClass.java.enumConstants as Array<Enum<*>>
        for (enumConstant in enumConstants) {
            val codeValue = ReflectUtil.getFieldValue(enumConstant, anno.codeField)
            val textValue = ReflectUtil.getFieldValue(enumConstant, anno.textField) as? String ?: ""
            enumValueMap[codeValue!!] = textValue
        }
        return enumValueMap
    }

    private fun getAnnotation(field: Field): ExcelEnumFormat {
        return AnnotationUtil.getAnnotation(field, ExcelEnumFormat::class.java)
    }
}
