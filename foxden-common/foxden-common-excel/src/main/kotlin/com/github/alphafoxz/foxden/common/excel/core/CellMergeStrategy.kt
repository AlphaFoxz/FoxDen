package com.github.alphafoxz.foxden.common.excel.core

import cn.hutool.core.collection.CollUtil
import com.alibaba.excel.metadata.Head
import com.alibaba.excel.write.handler.WorkbookWriteHandler
import com.alibaba.excel.write.handler.context.WorkbookWriteHandlerContext
import com.alibaba.excel.write.merge.AbstractMergeStrategy
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.CellRangeAddress
import org.slf4j.LoggerFactory

/**
 * 列值重复合并策略
 *
 * @author Lion Li
 */
class CellMergeStrategy : AbstractMergeStrategy, WorkbookWriteHandler {

    private val log = LoggerFactory.getLogger(CellMergeStrategy::class.java)

    private val cellList: List<CellRangeAddress>

    constructor(cellList: List<CellRangeAddress>) {
        this.cellList = cellList
    }

    constructor(list: List<*>, hasTitle: Boolean) {
        this.cellList = CellMergeHandler.of(hasTitle).handle(list)
    }

    override fun merge(sheet: Sheet, cell: Cell, head: Head, relativeRowIndex: Int?) {
        if (CollUtil.isEmpty(cellList)) {
            return
        }
        // 单元格写入了,遍历合并区域,如果该Cell在区域内,但非首行,则清空
        val rowIndex = cell.rowIndex
        for (cellAddresses in cellList) {
            val firstRow = cellAddresses.firstRow
            if (cellAddresses.isInRange(cell) && rowIndex != firstRow) {
                cell.setBlank()
            }
        }
    }

    override fun afterWorkbookDispose(context: WorkbookWriteHandlerContext) {
        if (CollUtil.isEmpty(cellList)) {
            return
        }
        // 当前表格写完后，统一写入
        for (item in cellList) {
            context.writeContext.writeSheetHolder().sheet.addMergedRegion(item)
        }
    }
}
