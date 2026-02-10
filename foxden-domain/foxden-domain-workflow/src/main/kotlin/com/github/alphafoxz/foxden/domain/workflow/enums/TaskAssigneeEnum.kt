package com.github.alphafoxz.foxden.domain.workflow.enums

import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.StringUtils

/**
 * 任务分配人枚举
 *
 * @author AprilWind
 */
enum class TaskAssigneeEnum(private val desc: String, private val code: String) {

    /**
     * 用户
     */
    USER("用户", ""),

    /**
     * 角色
     */
    ROLE("角色", "role:"),

    /**
     * 部门
     */
    DEPT("部门", "dept:"),

    /**
     * 岗位
     */
    POST("岗位", "post:"),

    /**
     * SPEL表达式
     */
    SPEL("SpEL表达式", "");

    companion object {
        /**
         * 根据描述获取对应的枚举类型
         *
         * @param desc 描述
         * @return TaskAssigneeEnum
         * @throws ServiceException 如果未找到匹配的枚举项
         */
        fun fromDesc(desc: String): TaskAssigneeEnum {
            return entries.find { it.desc == desc }
                ?: throw ServiceException("未知的办理人类型: $desc")
        }

        /**
         * 根据代码获取对应的枚举类型
         *
         * @param code 代码
         * @return TaskAssigneeEnum
         * @throws ServiceException 如果未找到匹配的枚举项
         */
        fun fromCode(code: String): TaskAssigneeEnum {
            return entries.find { it.code == code }
                ?: throw ServiceException("未知的办理人类型代码: $code")
        }

        /**
         * 获取所有办理人类型的描述列表
         *
         * @return 所有描述列表
         */
        fun getAssigneeTypeList(): List<String> = entries.map { it.desc }

        /**
         * 获取所有办理人类型的代码列表
         *
         * @return 所有代码列表
         */
        fun getAssigneeCodeList(): List<String> = entries.map { it.code }
    }

    fun getDesc(): String = desc
    fun getCode(): String = code

    /**
     * 判断当前办理人类型是否需要调用部门服务（deptService）
     *
     * @return 如果类型是 USER、DEPT 或 POST，则返回 true；否则返回 false
     */
    fun needsDeptService(): Boolean = this == USER || this == DEPT || this == POST

    /**
     * 判断给定字符串是否符合 SPEL 表达式格式（以 $ 或 # 开头）
     *
     * @param value 待判断字符串
     * @return 是否为 SPEL 表达式
     */
    fun isSpelExpression(value: String?): Boolean {
        if (value.isNullOrBlank()) {
            return false
        }
        // $前缀表示默认办理人变量策略
        // #前缀表示spel办理人变量策略
        return StringUtils.startsWith(value, "$") || StringUtils.startsWith(value, "#")
    }
}
