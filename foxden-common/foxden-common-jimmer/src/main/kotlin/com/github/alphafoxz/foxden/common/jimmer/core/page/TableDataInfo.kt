package com.github.alphafoxz.foxden.common.jimmer.core.page

import cn.hutool.core.collection.CollUtil
import cn.hutool.http.HttpStatus
import java.io.Serializable

/**
 * 表格分页数据对象
 */
class TableDataInfo<T> : Serializable {
    /**
     * 总记录数
     */
    var total: Long = 0

    /**
     * 列表数据
     */
    var rows: List<T> = mutableListOf()

    /**
     * 消息状态码
     */
    var code: Int = HttpStatus.HTTP_OK

    /**
     * 消息内容
     */
    var msg: String = "查询成功"

    constructor()

    constructor(list: List<T>, total: Long) {
        this.rows = list
        this.total = total
        this.code = HttpStatus.HTTP_OK
        this.msg = "查询成功"
    }

    companion object {
        /**
         * 根据数据列表构建表格分页数据对象
         */
        @JvmStatic
        fun <T> build(list: List<T>): TableDataInfo<T> {
            val rspData = TableDataInfo<T>()
            rspData.code = HttpStatus.HTTP_OK
            rspData.msg = "查询成功"
            rspData.rows = list
            rspData.total = list.size.toLong()
            return rspData
        }

        /**
         * 构建空表格分页数据对象
         */
        @JvmStatic
        fun <T> build(): TableDataInfo<T> {
            val rspData = TableDataInfo<T>()
            rspData.code = HttpStatus.HTTP_OK
            rspData.msg = "查询成功"
            return rspData
        }

        /**
         * 根据数据列表和分页参数构建表格分页数据对象（用于假分页）
         */
        @JvmStatic
        fun <T> build(list: List<T>, pageNum: Int, pageSize: Int): TableDataInfo<T> {
            if (CollUtil.isEmpty(list)) {
                return build()
            }
            val pageList = CollUtil.page(pageNum - 1, pageSize, list)
            return TableDataInfo(pageList, list.size.toLong())
        }
    }
}
