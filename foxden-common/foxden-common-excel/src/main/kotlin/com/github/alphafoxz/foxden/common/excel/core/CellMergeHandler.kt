package com.github.alphafoxz.foxden.common.excel.core

import cn.hutool.core.collection.CollUtil
import com.alibaba.excel.annotation.ExcelIgnore
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import org.apache.poi.ss.util.CellRangeAddress
import cn.hutool.core.util.ReflectUtil
import com.github.alphafoxz.foxden.common.excel.annotation.CellMerge
import java.lang.reflect.Field

/**
 * 单元格合并处理器
 *
 * @author Lion Li
 */
class CellMergeHandler private constructor(private val hasTitle: Boolean) {

    private var rowIndex: Int = if (hasTitle) 1 else 0

    fun handle(rows: List<*>): List<CellRangeAddress> {
        // 如果入参为空集合则返回空集
        if (CollUtil.isEmpty(rows)) {
            return emptyList()
        }

        // 获取有合并注解的字段
        val firstRow = rows[0] ?: return emptyList()
        val mergeFields = getFieldColumnIndexMap(firstRow::class.java)
        // 如果没有需要合并的字段则返回空集
        if (CollUtil.isEmpty(mergeFields)) {
            return emptyList()
        }

        // 结果集
        val result = mutableListOf<CellRangeAddress>()

        // 生成两两合并单元格
        val rowRepeatCellMap = mutableMapOf<Field, RepeatCell>()
        for ((field, itemValue) in mergeFields) {
            val colNum = itemValue.colIndex
            val cellMerge = itemValue.cellMerge

            for (i in rows.indices) {
                // 当前行数据
                val currentRowObj = rows[i]
                // 当前行数据字段值
                val currentRowObjFieldVal = ReflectUtil.getFieldValue(currentRowObj, field.name)

                // 空值跳过不处理
                if (currentRowObjFieldVal == null || "" == currentRowObjFieldVal) {
                    continue
                }

                // 单元格合并Map是否存在数据，如果不存在则添加当前行的字段值
                if (!rowRepeatCellMap.containsKey(field)) {
                    rowRepeatCellMap[field] = RepeatCell(currentRowObjFieldVal, i)
                    continue
                }

                // 获取 单元格合并Map 中字段值
                val repeatCell = rowRepeatCellMap[field]!!
                val cellValue = repeatCell.value
                val current = repeatCell.current

                // 检查是否满足合并条件
                val merge = isMerge(currentRowObj, rows[i - 1], cellMerge)

                // 是否添加到结果集
                var isAddResult = false
                // 最新行
                var lastRow = i + rowIndex - 1

                // 如果当前行字段值和缓存中的字段值不相等，或不满足合并条件，则替换
                if (currentRowObjFieldVal != cellValue || !merge) {
                    rowRepeatCellMap[field] = RepeatCell(currentRowObjFieldVal, i)
                    isAddResult = true
                }

                // 如果最后一行不能合并，检查之前的数据是否需要合并；如果最后一行可以合并，则直接合并到最后
                if (i == rows.size - 1) {
                    isAddResult = true
                    if (i > current) {
                        lastRow = i + rowIndex
                    }
                }

                if (isAddResult && i > current) {
                    result.add(CellRangeAddress(current + rowIndex, lastRow, colNum, colNum))
                }
            }
        }
        return result
    }

    /**
     * 获取带有合并注解的字段列索引和合并注解信息Map集
     */
    private fun getFieldColumnIndexMap(clazz: Class<*>): Map<Field, FieldColumnIndex> {
        val annotationPresent = clazz.isAnnotationPresent(ExcelIgnoreUnannotated::class.java)
        val fields = ReflectUtil.getFields(clazz)
        val filteredFields = mutableListOf<Field>()
        for (field in fields) {
            if ("serialVersionUID" == field.name) {
                continue
            }
            if (field.isAnnotationPresent(ExcelIgnore::class.java)) {
                continue
            }
            if (!annotationPresent || field.isAnnotationPresent(ExcelProperty::class.java)) {
                filteredFields.add(field)
            }
        }

        // 有注解的字段
        val mergeFields = mutableMapOf<Field, FieldColumnIndex>()
        for (i in filteredFields.indices) {
            val field = filteredFields[i]
            if (!field.isAnnotationPresent(CellMerge::class.java)) {
                continue
            }
            val cm = field.getAnnotation(CellMerge::class.java)
            val index = if (cm.index == -1) i else cm.index
            mergeFields[field] = FieldColumnIndex(index, cm)

            if (hasTitle) {
                val property = field.getAnnotation(ExcelProperty::class.java)
                rowIndex = maxOf(rowIndex, property.value.size)
            }
        }
        return mergeFields
    }

    private fun isMerge(currentRow: Any?, preRow: Any?, cellMerge: CellMerge): Boolean {
        val mergeBy = cellMerge.mergeBy
        if (mergeBy.isNotEmpty()) {
            // 比对当前行和上一行的各个属性值一一比对 如果全为真 则为真
            for (fieldName in mergeBy) {
                val valCurrent = ReflectUtil.getFieldValue(currentRow, fieldName)
                val valPre = ReflectUtil.getFieldValue(preRow, fieldName)
                if (valPre != valCurrent) {
                    // 依赖字段如有任一不等值,则标记为不可合并
                    return false
                }
            }
        }
        return true
    }

    /**
     * 单元格合并
     */
    data class RepeatCell(val value: Any?, val current: Int)

    /**
     * 字段列索引和合并注解信息
     */
    data class FieldColumnIndex(val colIndex: Int, val cellMerge: CellMerge)

    companion object {
        /**
         * 创建一个单元格合并处理器实例
         *
         * @param hasTitle 是否合并标题
         * @return 单元格合并处理器
         */
        @JvmStatic
        fun of(hasTitle: Boolean): CellMergeHandler {
            return CellMergeHandler(hasTitle)
        }

        /**
         * 创建一个单元格合并处理器实例（默认不合并标题）
         *
         * @return 单元格合并处理器
         */
        @JvmStatic
        fun of(): CellMergeHandler {
            return CellMergeHandler(false)
        }
    }
}
