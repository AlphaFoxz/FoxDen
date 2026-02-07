package com.github.alphafoxz.foxden.common.jimmer.enums

/**
 * 数据库类型枚举
 *
 * @author Lion Li
 */
enum class DataBaseType(
    val code: String,
    val description: String
) {
    /**
     * MySQL
     */
    MYSQL("mysql", "MySQL"),

    /**
     * PostgreSQL
     */
    POSTGRE_SQL("postgresql", "PostgreSQL"),

    /**
     * Oracle
     */
    ORACLE("oracle", "Oracle"),

    /**
     * H2
     */
    H2("h2", "H2"),

    /**
     * SQL Server
     */
    SQL_SERVER("sqlserver", "SQL Server");

    companion object {
        /**
         * 根据代码查找数据库类型
         */
        @JvmStatic
        fun findCode(code: String): DataBaseType? {
            return values().firstOrNull { it.code.equals(code, ignoreCase = true) }
        }
    }
}
