package com.github.alphafoxz.foxden.domain.system.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 操作日志视图对象 sys_oper_log
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ExcelIgnoreUnannotated
data class SysOperLogVo(
    /**
     * 日志主键
     */
    @ExcelProperty(value = ["日志主键"])
    var operId: Long? = null,

    /**
     * 模块标题
     */
    @ExcelProperty(value = ["操作模块"])
    var title: String? = null,

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    @ExcelProperty(value = ["业务类型"])
    var businessType: Int? = null,

    /**
     * 方法名称
     */
    @ExcelProperty(value = ["请求方法"])
    var method: String? = null,

    /**
     * 请求方式
     */
    @ExcelProperty(value = ["请求方式"])
    var requestMethod: String? = null,

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    @ExcelProperty(value = ["操作类别"])
    var operatorType: Int? = null,

    /**
     * 操作人员
     */
    @ExcelProperty(value = ["操作人员"])
    var operName: String? = null,

    /**
     * 部门名称
     */
    @ExcelProperty(value = ["部门名称"])
    var deptName: String? = null,

    /**
     * 请求URL
     */
    @ExcelProperty(value = ["请求地址"])
    var operUrl: String? = null,

    /**
     * 主机地址
     */
    @ExcelProperty(value = ["操作地址"])
    var operIp: String? = null,

    /**
     * 操作地点
     */
    @ExcelProperty(value = ["操作地点"])
    var operLocation: String? = null,

    /**
     * 请求参数
     */
    @ExcelProperty(value = ["请求参数"])
    var operParam: String? = null,

    /**
     * 返回参数
     */
    @ExcelProperty(value = ["返回参数"])
    var jsonResult: String? = null,

    /**
     * 操作状态（0正常 1异常）
     */
    @ExcelProperty(value = ["状态"])
    var status: Int? = null,

    /**
     * 错误消息
     */
    @ExcelProperty(value = ["错误消息"])
    var errorMsg: String? = null,

    /**
     * 操作时间
     */
    @ExcelProperty(value = ["操作时间"])
    var operTime: java.time.LocalDateTime? = null,

    /**
     * 消耗时间(毫秒)
     */
    @ExcelProperty(value = ["消耗时间"])
    var costTime: Long? = null
)
