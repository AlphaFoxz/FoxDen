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
import com.github.alphafoxz.foxden.common.core.service.DictService
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.excel.annotation.ExcelDictFormat
import com.github.alphafoxz.foxden.common.excel.utils.ExcelUtil
import org.slf4j.LoggerFactory
import java.lang.reflect.Field

/**
 * 字典格式化转换处理
 *
 * @author Lion Li
 */
class ExcelDictConvert : Converter<Any?> {
    private val log = LoggerFactory.getLogger(ExcelDictConvert::class.java)

    @Suppress("UNCHECKED_CAST")
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
        val anno = getAnnotation(contentProperty?.field ?: return null)
        val type = anno.dictType
        val label = cellData.stringValue
        val value: String?
        if (StringUtils.isBlank(type)) {
            value = ExcelUtil.reverseByExp(label, anno.readConverterExp, anno.separator)
        } else {
            value = SpringUtils.getBean(DictService::class.java)
                .getDictValue(type, label, anno.separator)
        }
        return Convert.convert<Any?>(contentProperty.field.type, value)
    }

    override fun convertToExcelData(
        `object`: Any?,
        contentProperty: ExcelContentProperty?,
        globalConfiguration: GlobalConfiguration?
    ): WriteCellData<String> {
        if (ObjectUtil.isNull(`object`)) {
            return WriteCellData("")
        }
        val anno = getAnnotation(contentProperty?.field ?: return WriteCellData(""))
        val type = anno.dictType
        val value = Convert.toStr(`object`)
        val label: String?
        if (StringUtils.isBlank(type)) {
            label = ExcelUtil.convertByExp(value, anno.readConverterExp, anno.separator)
        } else {
            label = SpringUtils.getBean(DictService::class.java)
                .getDictLabel(type, value, anno.separator)
        }
        return WriteCellData(label!!)
    }

    private fun getAnnotation(field: Field): ExcelDictFormat {
        return AnnotationUtil.getAnnotation(field, ExcelDictFormat::class.java)
    }
}
