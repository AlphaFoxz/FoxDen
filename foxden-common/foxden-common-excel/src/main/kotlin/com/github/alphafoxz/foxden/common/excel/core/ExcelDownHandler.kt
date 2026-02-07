package com.github.alphafoxz.foxden.common.excel.core

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.convert.Convert
import cn.hutool.core.util.ArrayUtil
import cn.hutool.core.util.EnumUtil
import cn.hutool.core.util.ObjectUtil
import cn.hutool.core.util.StrUtil
import com.alibaba.excel.metadata.FieldCache
import com.alibaba.excel.metadata.FieldWrapper
import com.alibaba.excel.util.ClassUtils
import com.alibaba.excel.write.handler.SheetWriteHandler
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.service.DictService
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StreamUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.excel.annotation.ExcelDictFormat
import com.github.alphafoxz.foxden.common.excel.annotation.ExcelEnumFormat
import org.slf4j.LoggerFactory
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddressList
import org.apache.poi.ss.util.WorkbookUtil
import org.apache.poi.xssf.usermodel.XSSFDataValidation
import java.lang.reflect.Field
import java.util.*

/**
 * Excel表格下拉选操作
 * 考虑到下拉选过多可能导致Excel打开缓慢的问题，只校验前1000行
 * 即只有前1000行的数据可以用下拉框，超出的自行通过限制数据量的形式，第二次输出
 *
 * @author Emil.Zhang
 */
class ExcelDownHandler(private val dropDownOptions: List<DropDownOptions>) : SheetWriteHandler {

    private val log = LoggerFactory.getLogger(ExcelDownHandler::class.java)

    private val dictService: DictService = SpringUtils.getBean(DictService::class.java)

    /**
     * 当前单选进度
     */
    private var currentOptionsColumnIndex = 0

    /**
     * 当前联动选择进度
     */
    private var currentLinkedOptionsSheetIndex = 0

    companion object {
        /**
         * Excel表格中的列名英文
         * 仅为了解析列英文，禁止修改
         */
        private const val EXCEL_COLUMN_NAME = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

        /**
         * 单选数据Sheet名
         */
        private const val OPTIONS_SHEET_NAME = "options"

        /**
         * 联动选择数据Sheet名的头
         */
        private const val LINKED_OPTIONS_SHEET_NAME = "linkedOptions"
    }

    /**
     * 开始创建下拉数据
     * 1.通过解析传入的@ExcelProperty同级是否标注有@DropDown选项
     * 如果有且设置了value值，则将其直接置为下拉可选项
     *
     * 2.或者在调用ExcelUtil时指定了可选项，将依据传入的可选项做下拉
     *
     * 3.二者并存，注意调用方式
     */
    override fun afterSheetCreate(writeWorkbookHolder: WriteWorkbookHolder, writeSheetHolder: WriteSheetHolder) {
        val sheet = writeSheetHolder.sheet
        // 开始设置下拉框 HSSFWorkbook
        val helper = sheet.dataValidationHelper
        val workbook = writeWorkbookHolder.workbook
        val fieldCache = ClassUtils.declaredFields(writeWorkbookHolder.clazz, writeWorkbookHolder)

        for ((index, wrapper) in fieldCache.sortedFieldMap) {
            val field = wrapper.field
            // 可选的下拉值
            var options: MutableList<String>? = null

            if (field.isAnnotationPresent(ExcelDictFormat::class.java)) {
                // 如果指定了@ExcelDictFormat，则使用字典的逻辑
                val format = field.getDeclaredAnnotation(ExcelDictFormat::class.java)
                val dictType = format.dictType
                val converterExp = format.readConverterExp
                if (StringUtils.isNotBlank(dictType)) {
                    // 如果传递了字典名，则依据字典建立下拉
                    val values = dictService.getAllDictByDictType(dictType)
                        ?: throw ServiceException("字典 $dictType 不存在")
                    options = values.values.toMutableList()
                } else if (StringUtils.isNotBlank(converterExp)) {
                    // 如果指定了确切的值，则直接解析确切的值
                    val strList = StringUtils.splitList(converterExp, format.separator)
                    options = strList.mapNotNull { s -> StringUtils.split(s, "=")?.getOrNull(1) }.toMutableList()
                }
            } else if (field.isAnnotationPresent(ExcelEnumFormat::class.java)) {
                // 否则如果指定了@ExcelEnumFormat，则使用枚举的逻辑
                val format = field.getDeclaredAnnotation(ExcelEnumFormat::class.java)
                val values = EnumUtil.getFieldValues(format.enumClass.java, format.textField)
                options = values.map { Convert.toStr(it) }.toMutableList()
            }

            if (ObjectUtil.isNotEmpty(options)) {
                // 仅当下拉可选项不为空时执行
                if (options!!.size > 20) {
                    // 这里限制如果可选项大于20，则使用额外表形式
                    dropDownWithSheet(helper, workbook, sheet, index, options)
                } else {
                    // 否则使用固定值形式
                    dropDownWithSimple(helper, sheet, index, options)
                }
            }
        }

        if (CollUtil.isNotEmpty(dropDownOptions)) {
            for (everyOptions in dropDownOptions) {
                // 如果传递了下拉框选择器参数
                if (everyOptions.nextOptions.isNotEmpty()) {
                    // 当二级选项不为空时，使用额外关联表的形式
                    dropDownLinkedOptions(helper, workbook, sheet, everyOptions)
                } else if (everyOptions.options.size > 10) {
                    // 当一级选项参数个数大于10，使用额外表的形式
                    dropDownWithSheet(helper, workbook, sheet, everyOptions.index, everyOptions.options)
                } else {
                    // 否则使用默认形式
                    dropDownWithSimple(helper, sheet, everyOptions.index, everyOptions.options)
                }
            }
        }
    }

    /**
     * 简单下拉框
     * 直接将可选项拼接为指定列的数据校验值
     *
     * @param helper 数据校验辅助类
     * @param sheet  工作表
     * @param celIndex 列index
     * @param value   下拉选可选值
     */
    private fun dropDownWithSimple(helper: DataValidationHelper, sheet: Sheet, celIndex: Int, value: List<String>) {
        if (ObjectUtil.isEmpty(value)) {
            return
        }
        this.markOptionsToSheet(helper, sheet, celIndex, helper.createExplicitListConstraint(value.toTypedArray()))
    }

    /**
     * 额外表格形式的级联下拉框
     *
     * @param options 额外表格形式存储的下拉可选项
     */
    private fun dropDownLinkedOptions(
        helper: DataValidationHelper,
        workbook: Workbook,
        sheet: Sheet,
        options: DropDownOptions
    ) {
        val linkedOptionsSheetName = String.format("%s_%d", LINKED_OPTIONS_SHEET_NAME, currentLinkedOptionsSheetIndex)
        // 创建联动下拉数据表
        val linkedOptionsDataSheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(linkedOptionsSheetName))
        // 将下拉表隐藏
        workbook.setSheetHidden(workbook.getSheetIndex(linkedOptionsDataSheet), true)

        // 选项数据
        val firstOptions = options.options
        val secoundOptionsMap = options.nextOptions.mapValues { it.value.toMutableList() }

        // 使用ArrayList记载数据，防止乱序
        val columnNames = mutableListOf<String>()
        // 写入第一行，即第一级的数据
        val firstRow = linkedOptionsDataSheet.createRow(0)
        for (columnIndex in firstOptions.indices) {
            val columnName = firstOptions[columnIndex]
            firstRow.createCell(columnIndex).setCellValue(columnName)
            columnNames.add(columnName)
        }

        // 创建名称管理器
        val name = workbook.createName()
        // 设置名称管理器的别名
        name.nameName = linkedOptionsSheetName
        // 以横向第一行创建一级下拉拼接引用位置
        val firstOptionsFunction = String.format(
            "%s!\$%s\$1:\$%s\$1",
            linkedOptionsSheetName,
            getExcelColumnName(0),
            getExcelColumnName(firstOptions.size)
        )
        // 设置名称管理器的引用位置
        name.setRefersToFormula(firstOptionsFunction)
        // 设置数据校验为序列模式，引用的是名称管理器中的别名
        this.markOptionsToSheet(helper, sheet, options.index, helper.createFormulaListConstraint(linkedOptionsSheetName))

        // 创建二级选项的名称管理器
        for (columIndex in columnNames.indices) {
            // 列名
            val firstOptionsColumnName = getExcelColumnName(columIndex)
            // 对应的一级值
            val thisFirstOptionsValue = columnNames[columIndex]

            // 以该一级选项值创建子名称管理器
            val sonName = workbook.createName()
            // 设置名称管理器的别名
            sonName.nameName = thisFirstOptionsValue
            // 以第二行该列数据拼接引用位置
            val sonFunction = String.format(
                "%s!\$%s\$2:\$%s\$%d",
                linkedOptionsSheetName,
                firstOptionsColumnName,
                firstOptionsColumnName,
                // 二级选项存在则设置为(选项个数+1)行，否则设置为2行
                maxOf(secoundOptionsMap[thisFirstOptionsValue]?.size ?: 0, 1) + 1
            )
            // 设置名称管理器的引用位置
            sonName.setRefersToFormula(sonFunction)

            // 数据验证为序列模式，引用到每一个主表中的二级选项位置
            val mainSheetFirstOptionsColumnName = getExcelColumnName(options.index)
            for (i in 0 until 100) {
                // 以一级选项对应的主体所在位置创建二级下拉
                val secondOptionsFunction = String.format("=INDIRECT(%s%d)", mainSheetFirstOptionsColumnName, i + 1)
                // 二级只能主表每一行的每一列添加二级校验
                markLinkedOptionsToSheet(helper, sheet, i, options.nextIndex, helper.createFormulaListConstraint(secondOptionsFunction))
            }
        }

        // 将二级数据处理为按行区分
        val columnValueMap = mutableMapOf<Int, List<String>>()
        var currentRow = 1
        while (currentRow >= 0) {
            var flag = false
            val rowData = mutableListOf<String>()
            for (columnName in columnNames) {
                val data = secoundOptionsMap[columnName]
                if (CollUtil.isEmpty(data)) {
                    // 添加空字符串填充位置
                    rowData.add(" ")
                    continue
                }
                // 取第一个
                val str = data!![0]
                rowData.add(str)
                // 通过移除的方式避免重复
                data.removeAt(0)
                // 设置可以继续
                flag = true
            }
            columnValueMap[currentRow] = rowData
            // 可以继续，则增加行数，否则置为负数跳出循环
            currentRow = if (flag) currentRow + 1 else -1
        }

        // 填充第二级选项数据
        columnValueMap.forEach { (rowIndex, rowValues) ->
            val row = linkedOptionsDataSheet.createRow(rowIndex)
            for (columnIndex in rowValues.indices) {
                val rowValue = rowValues[columnIndex]
                // 填充位置的部分不渲染
                if (StrUtil.isNotBlank(rowValue)) {
                    row.createCell(columnIndex).setCellValue(rowValue)
                }
            }
        }

        currentLinkedOptionsSheetIndex++
    }

    /**
     * 额外表格形式的普通下拉框
     * 由于下拉框可选值数量过多，为提升Excel打开效率，使用额外表格形式做下拉
     *
     * @param celIndex 下拉选
     * @param value    下拉选可选值
     */
    private fun dropDownWithSheet(
        helper: DataValidationHelper,
        workbook: Workbook,
        sheet: Sheet,
        celIndex: Int,
        value: List<String>
    ) {
        val tmpOptionsSheetName = OPTIONS_SHEET_NAME + "_" + currentOptionsColumnIndex
        // 创建下拉数据表
        val simpleDataSheet = workbook.getSheet(WorkbookUtil.createSafeSheetName(tmpOptionsSheetName))
            ?: workbook.createSheet(WorkbookUtil.createSafeSheetName(tmpOptionsSheetName))
        // 将下拉表隐藏
        workbook.setSheetHidden(workbook.getSheetIndex(simpleDataSheet), true)

        // 完善纵向的一级选项数据表
        for (i in value.indices) {
            // 获取每一选项行，如果没有则创建
            val row = simpleDataSheet.getRow(i) ?: simpleDataSheet.createRow(i)
            // 获取本级选项对应的选项列，如果没有则创建。上述采用多个sheet,默认索引为1列
            val cell = row.getCell(0) ?: row.createCell(0)
            // 设置值
            cell.setCellValue(value[i])
        }

        // 创建名称管理器
        val name = workbook.createName()
        // 设置名称管理器的别名
        val nameName = String.format("%s_%d", tmpOptionsSheetName, celIndex)
        name.nameName = nameName
        // 以纵向第一列创建一级下拉拼接引用位置
        val function = String.format(
            "%s!\$%s\$1:\$%s\$%d",
            tmpOptionsSheetName,
            getExcelColumnName(0),
            getExcelColumnName(0),
            value.size
        )
        // 设置名称管理器的引用位置
        name.setRefersToFormula(function)
        // 设置数据校验为序列模式，引用的是名称管理器中的别名
        this.markOptionsToSheet(helper, sheet, celIndex, helper.createFormulaListConstraint(nameName))
        currentOptionsColumnIndex++
    }

    /**
     * 挂载下拉的列，仅限一级选项
     */
    private fun markOptionsToSheet(helper: DataValidationHelper, sheet: Sheet, celIndex: Int, constraint: DataValidationConstraint) {
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        val addressList = CellRangeAddressList(1, 1000, celIndex, celIndex)
        markDataValidationToSheet(helper, sheet, constraint, addressList)
    }

    /**
     * 挂载下拉的列，仅限二级选项
     */
    private fun markLinkedOptionsToSheet(
        helper: DataValidationHelper,
        sheet: Sheet,
        rowIndex: Int,
        celIndex: Int,
        constraint: DataValidationConstraint
    ) {
        // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
        val addressList = CellRangeAddressList(rowIndex, rowIndex, celIndex, celIndex)
        markDataValidationToSheet(helper, sheet, constraint, addressList)
    }

    /**
     * 应用数据校验
     */
    private fun markDataValidationToSheet(
        helper: DataValidationHelper,
        sheet: Sheet,
        constraint: DataValidationConstraint,
        addressList: CellRangeAddressList
    ) {
        // 数据有效性对象
        val dataValidation = helper.createValidation(constraint, addressList)
        // 处理Excel兼容性问题
        if (dataValidation is XSSFDataValidation) {
            // 数据校验
            dataValidation.suppressDropDownArrow = true
            // 错误提示
            dataValidation.errorStyle = DataValidation.ErrorStyle.STOP
            dataValidation.createErrorBox("提示", "此值与单元格定义数据不一致")
            dataValidation.showErrorBox = true
            // 选定提示
            dataValidation.createPromptBox("填写说明：", "填写内容只能为下拉中数据，其他数据将导致导入失败")
            dataValidation.showPromptBox = true
        } else {
            dataValidation.suppressDropDownArrow = false
        }
        sheet.addValidationData(dataValidation)
    }

    /**
     * 依据列index获取列名英文
     * 依据列index转换为Excel中的列名英文
     * 例如第1列，index为0，解析出来为A列
     * 第27列，index为26，解析为AA列
     * 第28列，index为27，解析为AB列
     *
     * @param columnIndex 列index
     * @return 列index所在得英文名
     */
    private fun getExcelColumnName(columnIndex: Int): String {
        // 26一循环的次数
        val columnCircleCount = columnIndex / 26
        // 26一循环内的位置
        val thisCircleColumnIndex = columnIndex % 26
        // 26一循环的次数大于0，则视为栏名至少两位
        val columnPrefix = if (columnCircleCount == 0) {
            StrUtil.EMPTY
        } else {
            StrUtil.subWithLength(EXCEL_COLUMN_NAME, columnCircleCount - 1, 1)
        }
        // 从26一循环内取对应的栏位名
        val columnNext = StrUtil.subWithLength(EXCEL_COLUMN_NAME, thisCircleColumnIndex, 1)
        // 将二者拼接即为最终的栏位名
        return columnPrefix + columnNext
    }
}
