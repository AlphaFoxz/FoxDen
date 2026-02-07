package com.github.alphafoxz.foxden.common.excel.utils

import com.alibaba.excel.ExcelWriter
import com.alibaba.excel.EasyExcel
import com.alibaba.excel.context.WriteContext
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder
import com.alibaba.excel.write.builder.ExcelWriterTableBuilder
import com.alibaba.excel.write.metadata.WriteSheet
import com.alibaba.excel.write.metadata.WriteTable
import com.alibaba.excel.write.metadata.fill.FillConfig
import java.util.function.Supplier

/**
 * ExcelWriterWrapper Excel写出包装器
 * 提供了一组与 ExcelWriter 一一对应的写出方法，避免直接提供 ExcelWriter 而导致的一些不可控问题（比如提前关闭了IO流等）
 *
 * @author 秋辞未寒
 */
class ExcelWriterWrapper<T>(private val excelWriter: ExcelWriter) {

    fun write(data: Collection<T>, writeSheet: WriteSheet) {
        excelWriter.write(data, writeSheet)
    }

    fun write(supplier: Supplier<Collection<T>>, writeSheet: WriteSheet) {
        excelWriter.write(supplier.get(), writeSheet)
    }

    fun write(data: Collection<T>, writeSheet: WriteSheet, writeTable: WriteTable) {
        excelWriter.write(data, writeSheet, writeTable)
    }

    fun write(supplier: Supplier<Collection<T>>, writeSheet: WriteSheet, writeTable: WriteTable) {
        excelWriter.write(supplier.get(), writeSheet, writeTable)
    }

    fun fill(data: Any, writeSheet: WriteSheet) {
        excelWriter.fill(data, writeSheet)
    }

    fun fill(data: Any, fillConfig: FillConfig, writeSheet: WriteSheet) {
        excelWriter.fill(data, fillConfig, writeSheet)
    }

    fun fill(supplier: Supplier<Any>, writeSheet: WriteSheet) {
        excelWriter.fill(supplier.get(), writeSheet)
    }

    fun fill(supplier: Supplier<Any>, fillConfig: FillConfig, writeSheet: WriteSheet) {
        excelWriter.fill(supplier.get(), fillConfig, writeSheet)
    }

    fun writeContext(): WriteContext {
        return excelWriter.writeContext()
    }

    companion object {
        /**
         * 创建一个 ExcelWriterWrapper
         *
         * @param excelWriter ExcelWriter
         * @return ExcelWriterWrapper
         */
        @JvmStatic
        fun <T> of(excelWriter: ExcelWriter): ExcelWriterWrapper<T> {
            return ExcelWriterWrapper(excelWriter)
        }

        // -------------------------------- sheet start

        @JvmStatic
        fun buildSheet(sheetNo: Int?, sheetName: String?): WriteSheet {
            return sheetBuilder(sheetNo, sheetName).build()
        }

        @JvmStatic
        fun buildSheet(sheetNo: Int?): WriteSheet {
            return sheetBuilder(sheetNo).build()
        }

        @JvmStatic
        fun buildSheet(sheetName: String?): WriteSheet {
            return sheetBuilder(sheetName).build()
        }

        @JvmStatic
        fun buildSheet(): WriteSheet {
            return sheetBuilder().build()
        }

        @JvmStatic
        fun sheetBuilder(sheetNo: Int?, sheetName: String?): ExcelWriterSheetBuilder {
            return EasyExcel.writerSheet(sheetNo, sheetName)
        }

        @JvmStatic
        fun sheetBuilder(sheetNo: Int?): ExcelWriterSheetBuilder {
            return EasyExcel.writerSheet(sheetNo)
        }

        @JvmStatic
        fun sheetBuilder(sheetName: String?): ExcelWriterSheetBuilder {
            return EasyExcel.writerSheet(sheetName)
        }

        @JvmStatic
        fun sheetBuilder(): ExcelWriterSheetBuilder {
            return EasyExcel.writerSheet()
        }

        // -------------------------------- sheet end

        // -------------------------------- table start

        @JvmStatic
        fun buildTable(tableNo: Int?): WriteTable {
            return tableBuilder(tableNo).build()
        }

        @JvmStatic
        fun buildTable(): WriteTable {
            return tableBuilder().build()
        }

        @JvmStatic
        fun tableBuilder(tableNo: Int?): ExcelWriterTableBuilder {
            return EasyExcel.writerTable(tableNo)
        }

        @JvmStatic
        fun tableBuilder(): ExcelWriterTableBuilder {
            return EasyExcel.writerTable()
        }

        // -------------------------------- table end
    }
}
