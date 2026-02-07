package com.github.alphafoxz.foxden.common.jimmer.utils

import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo

/**
 * 分页工具类
 */
object PageUtils {

    /**
     * 获取分页参数
     */
    @JvmStatic
    fun getPageNum(pageQuery: PageQuery): Int {
        return pageQuery.getPageNumOrDefault()
    }

    /**
     * 获取每页大小
     */
    @JvmStatic
    fun getPageSize(pageQuery: PageQuery): Int {
        return pageQuery.getPageSizeOrDefault()
    }

    /**
     * 构建分页数据
     */
    @JvmStatic
    fun <T> build(list: List<T>, total: Long): TableDataInfo<T> {
        return TableDataInfo(list, total)
    }

    /**
     * 构建空分页数据
     */
    @JvmStatic
    fun <T> empty(): TableDataInfo<T> {
        return TableDataInfo.build()
    }

    /**
     * 获取排序SQL
     */
    @JvmStatic
    fun getOrderBySql(pageQuery: PageQuery): String? {
        return pageQuery.buildOrderBySql()
    }
}
