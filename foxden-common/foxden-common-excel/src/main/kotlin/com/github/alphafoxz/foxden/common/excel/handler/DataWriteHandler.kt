package com.github.alphafoxz.foxden.common.excel.handler

import cn.hutool.core.collection.CollUtil
import com.alibaba.excel.annotation.ExcelProperty
import com.alibaba.excel.metadata.data.DataFormatData
import com.alibaba.excel.metadata.data.WriteCellData
import com.alibaba.excel.util.StyleUtil
import com.alibaba.excel.write.handler.CellWriteHandler
import com.alibaba.excel.write.handler.SheetWriteHandler
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder
import com.alibaba.excel.write.metadata.style.WriteCellStyle
import com.alibaba.excel.write.metadata.style.WriteFont
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFClientAnchor
import org.apache.poi.xssf.usermodel.XSSFRichTextString
import com.github.alphafoxz.foxden.common.excel.annotation.ExcelNotation
import com.github.alphafoxz.foxden.common.excel.annotation.ExcelRequired
import java.lang.reflect.Field

/**
 * 批注、必填
 *
 * @author guzhouyanyu
 */
class DataWriteHandler(clazz: Class<*>) : SheetWriteHandler, CellWriteHandler {

    /**
     * 批注
     */
    private val notationMap: Map<String, String>

    /**
     * 头列字体颜色
     */
    private val headColumnMap: Map<String, Short>

    init {
        notationMap = getNotationMap(clazz)
        headColumnMap = getRequiredMap(clazz)
    }

    override fun afterCellDispose(context: CellWriteHandlerContext) {
        if (CollUtil.isEmpty(notationMap) && CollUtil.isEmpty(headColumnMap)) {
            return
        }
        // 第一行
        val cellData = context.firstCellData
        // 第一个格子
        val writeCellStyle = cellData.getOrCreateStyle()

        if (context.head) {
            val dataFormatData = DataFormatData()
            // 单元格设置为文本格式
            dataFormatData.index = 49.toShort()
            writeCellStyle.setDataFormatData(dataFormatData)
            val cell = context.cell
            val writeSheetHolder = context.writeSheetHolder
            val sheet = writeSheetHolder.sheet
            val workbook = writeSheetHolder.sheet.workbook
            val drawing = sheet.createDrawingPatriarch()
            // 设置标题字体样式
            val headWriteFont = WriteFont()
            // 加粗
            headWriteFont.bold = true
            if (CollUtil.isNotEmpty(headColumnMap) && headColumnMap.containsKey(cell.stringCellValue)) {
                // 设置字体颜色
                headWriteFont.color = headColumnMap[cell.stringCellValue]!!
            }
            writeCellStyle.setWriteFont(headWriteFont)
            val cellStyle = StyleUtil.buildCellStyle(workbook, null, writeCellStyle)
            cell.cellStyle = cellStyle

            if (CollUtil.isNotEmpty(notationMap) && notationMap.containsKey(cell.stringCellValue)) {
                // 批注内容
                val notationContext = notationMap[cell.stringCellValue]!!
                // 创建绘图对象
                val comment = drawing.createCellComment(
                    XSSFClientAnchor(0, 0, 0, 0, cell.columnIndex, 0, 5, 5)
                )
                comment.setString(XSSFRichTextString(notationContext))
                cell.cellComment = comment
            }
        }
    }

    companion object {
        /**
         * 获取必填列
         */
        private fun getRequiredMap(clazz: Class<*>): Map<String, Short> {
            val requiredMap = mutableMapOf<String, Short>()
            val fields = clazz.declaredFields
            for (field in fields) {
                if (!field.isAnnotationPresent(ExcelRequired::class.java)) {
                    continue
                }
                val excelRequired = field.getAnnotation(ExcelRequired::class.java)
                val excelProperty = field.getAnnotation(ExcelProperty::class.java)
                requiredMap[excelProperty.value[0]] = excelRequired.fontColor.index
            }
            return requiredMap
        }

        /**
         * 获取批注
         */
        private fun getNotationMap(clazz: Class<*>): Map<String, String> {
            val notationMap = mutableMapOf<String, String>()
            val fields = clazz.declaredFields
            for (field in fields) {
                if (!field.isAnnotationPresent(ExcelNotation::class.java)) {
                    continue
                }
                val excelNotation = field.getAnnotation(ExcelNotation::class.java)
                val excelProperty = field.getAnnotation(ExcelProperty::class.java)
                notationMap[excelProperty.value[0]] = excelNotation.value
            }
            return notationMap
        }
    }
}
