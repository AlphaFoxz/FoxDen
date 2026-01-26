package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.LocalDateTime
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.hibernate.validator.constraints.Length

/**
 * 操作日志记录
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_oper_log")
interface SysOperLog : CommDelFlag, CommId, CommTenant {
    /**
     * 模块标题
     */
    @Column(name = "title")
    @get:Length(max = 2147483647)
    val title: String?

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    @Column(name = "business_type")
    @get:Max(value = 2147483647, message = "业务类型（0其它 1新增 2修改 3删除）不可大于2147483647")
    @get:Min(value = 0, message = "业务类型（0其它 1新增 2修改 3删除）不可小于0")
    val businessType: Int?

    /**
     * 方法名称
     */
    @Column(name = "method")
    @get:Length(max = 2147483647)
    val method: String?

    /**
     * 请求方式
     */
    @Column(name = "request_method")
    @get:Length(max = 2147483647)
    val requestMethod: String?

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    @Column(name = "operator_type")
    @get:Max(value = 2147483647, message = "操作类别（0其它 1后台用户 2手机端用户）不可大于2147483647")
    @get:Min(value = 0, message = "操作类别（0其它 1后台用户 2手机端用户）不可小于0")
    val operatorType: Int?

    /**
     * 操作人员
     */
    @Column(name = "oper_name")
    @get:Length(max = 2147483647)
    val operName: String?

    /**
     * 部门名称
     */
    @Column(name = "dept_name")
    @get:Length(max = 2147483647)
    val deptName: String?

    /**
     * 请求URL
     */
    @Column(name = "oper_url")
    @get:Length(max = 2147483647)
    val operUrl: String?

    /**
     * 主机地址
     */
    @Column(name = "oper_ip")
    @get:Length(max = 2147483647)
    val operIp: String?

    /**
     * 操作地点
     */
    @Column(name = "oper_location")
    @get:Length(max = 2147483647)
    val operLocation: String?

    /**
     * 请求参数
     */
    @Column(name = "oper_param")
    @get:Length(max = 2147483647)
    val operParam: String?

    /**
     * 返回参数
     */
    @Column(name = "json_result")
    @get:Length(max = 2147483647)
    val jsonResult: String?

    /**
     * 操作状态（0正常 1异常）
     */
    @Column(name = "status")
    @get:Max(value = 2147483647, message = "操作状态（0正常 1异常）不可大于2147483647")
    @get:Min(value = 0, message = "操作状态（0正常 1异常）不可小于0")
    val status: Int?

    /**
     * 错误消息
     */
    @Column(name = "error_msg")
    @get:Length(max = 2147483647)
    val errorMsg: String?

    /**
     * 操作时间
     */
    @Column(name = "oper_time")
    val operTime: LocalDateTime

    /**
     * 消耗时间
     */
    @Column(name = "cost_time")
    @get:Max(value = 9223372036854775807, message = "消耗时间不可大于9223372036854775807")
    @get:Min(value = 0, message = "消耗时间不可小于0")
    val costTime: Long?
}
