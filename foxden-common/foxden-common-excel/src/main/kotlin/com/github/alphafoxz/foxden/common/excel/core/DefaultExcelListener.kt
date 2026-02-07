package com.github.alphafoxz.foxden.common.excel.core

import cn.hutool.core.util.StrUtil
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.event.AnalysisEventListener
import com.alibaba.excel.exception.ExcelDataConvertException
import com.github.alphafoxz.foxden.common.core.utils.StreamUtils
import com.github.alphafoxz.foxden.common.core.utils.ValidatorUtils
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory

/**
 * Excel 导入监听
 *
 * @author Yjoioooo
 * @author Lion Li
 */
class DefaultExcelListener<T>(isValidate: Boolean = true) : AnalysisEventListener<T>(), ExcelListener<T> {

    private val log = LoggerFactory.getLogger(DefaultExcelListener::class.java)

    /**
     * 是否Validator检验，默认为是
     */
    private val isValidateFlag: Boolean = isValidate

    /**
     * excel 表头数据
     */
    private var headMap: Map<Int, String>? = null

    /**
     * 导入回执
     */
    private val excelResult: DefaultExcelResult<T> = DefaultExcelResult()

    /**
     * 处理异常
     *
     * @param exception ExcelDataConvertException
     * @param context   Excel 上下文
     */
    override fun onException(exception: Exception, context: AnalysisContext) {
        var errMsg: String? = null
        if (exception is ExcelDataConvertException) {
            // 如果是某一个单元格的转换异常 能获取到具体行号
            val rowIndex = exception.rowIndex
            val columnIndex = exception.columnIndex
            errMsg = StrUtil.format(
                "第{}行-第{}列-表头{}: 解析异常<br/>",
                rowIndex + 1, columnIndex + 1, headMap?.get(columnIndex)
            )
            if (log.isDebugEnabled) {
                log.error(errMsg)
            }
        }
        if (exception is ConstraintViolationException) {
            val constraintViolations = exception.constraintViolations
            @Suppress("UNCHECKED_CAST")
            val constraintViolationsMsg = StreamUtils.join(
                constraintViolations as Set<ConstraintViolation<Any>>,
                ConstraintViolation<Any>::getMessage,
                ", "
            )
            errMsg = StrUtil.format("第{}行数据校验异常: {}", context.readRowHolder().rowIndex + 1, constraintViolationsMsg)
            if (log.isDebugEnabled) {
                log.error(errMsg)
            }
        }
        excelResult.addError(errMsg ?: "未知异常")
        throw Exception(errMsg)
    }

    override fun invokeHeadMap(headMap: Map<Int, String>, context: AnalysisContext) {
        this.headMap = headMap
        log.debug("解析到一条表头数据: {}", JsonUtils.toJsonString(headMap))
    }

    override fun invoke(data: T, context: AnalysisContext) {
        if (isValidateFlag) {
            ValidatorUtils.validate(data)
        }
        excelResult.addData(data)
    }

    override fun doAfterAllAnalysed(context: AnalysisContext) {
        log.debug("所有数据解析完成！")
    }

    override fun getExcelResult(): ExcelResult<T> = excelResult
}
