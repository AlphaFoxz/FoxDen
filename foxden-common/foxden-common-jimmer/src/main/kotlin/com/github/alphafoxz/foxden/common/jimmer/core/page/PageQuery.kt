package com.github.alphafoxz.foxden.common.jimmer.core.page

import cn.hutool.core.text.StrFormatter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.MessageUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import java.io.Serializable

/**
 * 分页查询实体类
 */
open class PageQuery : Serializable {
    /**
     * 分页大小
     */
    var pageSize: Int? = null

    /**
     * 当前页数
     */
    var pageNum: Int? = null

    /**
     * 排序列
     */
    var orderByColumn: String? = null

    /**
     * 排序的方向desc或者asc
     */
    var isAsc: String? = null

    /**
     * 当前记录起始索引 默认值
     */
    val firstNum: Int?
        @JsonIgnore
        get() {
            return if (pageNum != null && pageSize != null) {
                (pageNum!! - 1) * pageSize!!
            } else null
        }

    companion object {
        const val DEFAULT_PAGE_NUM = 1
        const val DEFAULT_PAGE_SIZE = Int.MAX_VALUE
    }

    /**
     * 构建排序SQL
     *
     * 支持的用法如下:
     * {isAsc:"asc",orderByColumn:"id"} order by id asc
     * {isAsc:"asc",orderByColumn:"id,createTime"} order by id asc,create_time asc
     * {isAsc:"desc",orderByColumn:"id,createTime"} order by id desc,create_time desc
     * {isAsc:"asc,desc",orderByColumn:"id,createTime"} order by id asc,create_time desc
     */
    fun buildOrderBySql(): String? {
        val orderBy = orderByColumn ?: return null
        val asc = isAsc ?: return null

        // 转换为下划线命名
        val orderByColumnStr = StringUtils.toUnderScoreCase(orderBy) ?: return null

        // 兼容前端排序类型
        val isAscStr = asc
            .replace("ascending", "asc", ignoreCase = true)
            .replace("descending", "desc", ignoreCase = true)

        val orderByArr = orderByColumnStr.split(StringUtils.SEPARATOR)
        val isAscArr = isAscStr.split(StringUtils.SEPARATOR)

        if (isAscArr.size != 1 && isAscArr.size != orderByArr.size) {
            throw ServiceException(MessageUtils.message("page.query.sort.error"))
        }

        val orders = mutableListOf<String>()
        for (i in orderByArr.indices) {
            val orderByCol = orderByArr[i]
            val ascCol = if (isAscArr.size == 1) isAscArr[0] else isAscArr[i]
            if ("asc".equals(ascCol, ignoreCase = true)) {
                orders.add("$orderByCol ASC")
            } else if ("desc".equals(ascCol, ignoreCase = true)) {
                orders.add("$orderByCol DESC")
            } else {
                throw ServiceException(MessageUtils.message("page.query.sort.error"))
            }
        }
        return orders.joinToString(", ")
    }

    /**
     * 获取当前页码，如果为空则返回默认值
     */
    fun getPageNumOrDefault(): Int {
        return if (pageNum != null && pageNum!! > 0) pageNum!! else DEFAULT_PAGE_NUM
    }

    /**
     * 获取每页大小，如果为空则返回默认值
     */
    fun getPageSizeOrDefault(): Int {
        return pageSize ?: DEFAULT_PAGE_SIZE
    }
}
