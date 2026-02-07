package com.github.alphafoxz.foxden.common.core.enums

import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import cn.hutool.core.util.StrUtil

/**
 * 业务状态枚举
 */
enum class BusinessStatusEnum(val status: String, val desc: String) {
    /** 已撤销 */
    CANCEL("cancel", "已撤销"),

    /** 草稿 */
    DRAFT("draft", "草稿"),

    /** 待审核 */
    WAITING("waiting", "待审核"),

    /** 已完成 */
    FINISH("finish", "已完成"),

    /** 已作废 */
    INVALID("invalid", "已作废"),

    /** 已退回 */
    BACK("back", "已退回"),

    /** 已终止 */
    TERMINATION("termination", "已终止");

    companion object {
        private val STATUS_MAP = entries.associateBy { it.status }

        /**
         * 根据状态获取对应的 BusinessStatusEnum 枚举
         */
        @JvmStatic
        fun getByStatus(status: String?): BusinessStatusEnum? = STATUS_MAP[status]

        /**
         * 根据状态获取对应的业务状态描述信息
         */
        @JvmStatic
        fun findByStatus(status: String?): String {
            if (StringUtils.isBlank(status)) return StrUtil.EMPTY
            return STATUS_MAP[status]?.desc ?: StrUtil.EMPTY
        }

        /**
         * 判断是否为指定的状态之一：草稿、已撤销或已退回
         */
        @JvmStatic
        fun isDraftOrCancelOrBack(status: String?): Boolean {
            return status == DRAFT.status || status == CANCEL.status || status == BACK.status
        }

        /**
         * 判断是否为撤销、退回、作废、终止
         */
        @JvmStatic
        fun initialState(status: String?): Boolean {
            return status == CANCEL.status ||
                    status == BACK.status ||
                    status == INVALID.status ||
                    status == TERMINATION.status
        }

        /**
         * 获取运行中的实例状态列表
         */
        @JvmStatic
        fun runningStatus(): List<String> = listOf(DRAFT.status, WAITING.status, BACK.status, CANCEL.status)

        /**
         * 获取结束实例的状态列表
         */
        @JvmStatic
        fun finishStatus(): List<String> = listOf(FINISH.status, INVALID.status, TERMINATION.status)

        /**
         * 启动流程校验
         */
        @JvmStatic
        fun checkStartStatus(status: String?) {
            when (status) {
                WAITING.status -> throw ServiceException("该单据已提交过申请,正在审批中！")
                FINISH.status -> throw ServiceException("该单据已完成申请！")
                INVALID.status -> throw ServiceException("该单据已作废！")
                TERMINATION.status -> throw ServiceException("该单据已终止！")
                else -> {
                    if (StringUtils.isBlank(status)) throw ServiceException("流程状态为空！")
                }
            }
        }

        /**
         * 撤销流程校验
         */
        @JvmStatic
        fun checkCancelStatus(status: String?) {
            when (status) {
                CANCEL.status -> throw ServiceException("该单据已撤销！")
                FINISH.status -> throw ServiceException("该单据已完成申请！")
                INVALID.status -> throw ServiceException("该单据已作废！")
                TERMINATION.status -> throw ServiceException("该单据已终止！")
                BACK.status -> throw ServiceException("该单据已退回！")
                else -> {
                    if (StringUtils.isBlank(status)) throw ServiceException("流程状态为空！")
                }
            }
        }

        /**
         * 驳回流程校验
         */
        @JvmStatic
        fun checkBackStatus(status: String?) {
            when (status) {
                BACK.status -> throw ServiceException("该单据已退回！")
                FINISH.status -> throw ServiceException("该单据已完成申请！")
                INVALID.status -> throw ServiceException("该单据已作废！")
                TERMINATION.status -> throw ServiceException("该单据已终止！")
                CANCEL.status -> throw ServiceException("该单据已撤销！")
                else -> {
                    if (StringUtils.isBlank(status)) throw ServiceException("流程状态为空！")
                }
            }
        }

        /**
         * 作废、终止流程校验
         */
        @JvmStatic
        fun checkInvalidStatus(status: String?) {
            when (status) {
                FINISH.status -> throw ServiceException("该单据已完成申请！")
                INVALID.status -> throw ServiceException("该单据已作废！")
                TERMINATION.status -> throw ServiceException("该单据已终止！")
                else -> {
                    if (StringUtils.isBlank(status)) throw ServiceException("流程状态为空！")
                }
            }
        }
    }
}
