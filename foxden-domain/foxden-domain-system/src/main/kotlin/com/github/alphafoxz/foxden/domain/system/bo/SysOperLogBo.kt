package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * 操作日志业务对象 sys_oper_log
 *
 * @author Lion Li
 */
data class SysOperLogBo(
    /**
     * 日志主键
     */
    var operId: Long? = null,

    /**
     * 模块标题
     */
    @get:NotBlank(message = "模块标题不能为空")
    var title: String? = null,

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    @get:NotNull(message = "业务类型不能为空")
    var businessType: Int? = null,

    /**
     * 方法名称
     */
    @get:Size(min = 0, max = 100, message = "方法名称长度不能超过{max}个字符")
    var method: String? = null,

    /**
     * 请求方式
     */
    @get:Size(min = 0, max = 10, message = "请求方式长度不能超过{max}个字符")
    var requestMethod: String? = null,

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    @get:NotNull(message = "操作类别不能为空")
    var operatorType: Int? = null,

    /**
     * 操作人员
     */
    @get:Size(min = 0, max = 50, message = "操作人员名称不能超过{max}个字符")
    var operName: String? = null,

    /**
     * 部门名称
     */
    @get:Size(min = 0, max = 50, message = "部门名称长度不能超过{max}个字符")
    var deptName: String? = null,

    /**
     * 请求URL
     */
    @get:Size(min = 0, max = 255, message = "请求URL长度不能超过{max}个字符")
    var operUrl: String? = null,

    /**
     * 主机地址
     */
    @get:Size(min = 0, max = 128, message = "主机地址长度不能超过{max}个字符")
    var operIp: String? = null,

    /**
     * 操作地点
     */
    @get:Size(min = 0, max = 255, message = "操作地点长度不能超过{max}个字符")
    var operLocation: String? = null,

    /**
     * 请求参数
     */
    var operParam: String? = null,

    /**
     * 返回参数
     */
    var jsonResult: String? = null,

    /**
     * 操作状态（0正常 1异常）
     */
    var status: Int? = null,

    /**
     * 错误消息
     */
    @get:Size(min = 0, max = 2000, message = "错误消息长度不能超过{max}个字符")
    var errorMsg: String? = null,

    /**
     * 操作时间
     */
    var operTime: java.time.LocalDateTime? = null,

    /**
     * 消耗时间(毫秒)
     */
    var costTime: Long? = null
)
