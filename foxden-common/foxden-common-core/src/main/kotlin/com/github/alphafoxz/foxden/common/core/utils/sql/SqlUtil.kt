package com.github.alphafoxz.foxden.common.core.utils.sql

import cn.hutool.core.util.StrUtil
import com.github.alphafoxz.foxden.common.core.utils.StringUtils

/**
 * sql操作工具类
 */
object SqlUtil {
    /**
     * 定义常用的 sql关键字
     */
    const val SQL_REGEX = "\u000B|and |extractvalue|updatexml|sleep|exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |or |union |like |+|/*|user()"

    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    const val SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+"

    /**
     * 检查字符，防止注入绕过
     */
    fun escapeOrderBySql(value: String?): String? {
        if (StringUtils.isNotEmpty(value) && !isValidOrderBySql(value!!)) {
            throw IllegalArgumentException("参数不符合规范，不能进行查询")
        }
        return value
    }

    /**
     * 验证 order by 语法是否符合规范
     */
    fun isValidOrderBySql(value: String): Boolean {
        return value.matches(SQL_PATTERN.toRegex())
    }

    /**
     * SQL关键字检查
     */
    fun filterKeyword(value: String?) {
        if (StringUtils.isEmpty(value)) {
            return
        }
        val sqlKeywords = StrUtil.split(SQL_REGEX, "\\|")
        for (sqlKeyword in sqlKeywords) {
            if (StringUtils.containsAnyIgnoreCase(value, sqlKeyword)) {
                throw IllegalArgumentException("参数存在SQL注入风险")
            }
        }
    }
}
