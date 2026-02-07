package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*
import java.util.Date

/**
 * 操作日志记录表 sys_oper_log
 */
@Entity
interface SysOperLog : CommId {
    /**
     * 租户编号
     */
    val tenantId: String?

    /**
     * 操作模块
     */
    val title: String?

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    val businessType: Int?

    /**
     * 请求方法
     */
    val method: String?

    /**
     * 请求方式
     */
    val requestMethod: String?

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    val operatorType: Int?

    /**
     * 操作人员
     */
    val operName: String?

    /**
     * 部门名称
     */
    val deptName: String?

    /**
     * 请求url
     */
    val operUrl: String?

    /**
     * 操作地址
     */
    val operIp: String?

    /**
     * 操作地点
     */
    val operLocation: String?

    /**
     * 请求参数
     */
    val operParam: String?

    /**
     * 返回参数
     */
    val jsonResult: String?

    /**
     * 操作状态（0正常 1异常）
     */
    val status: Int?

    /**
     * 错误消息
     */
    val errorMsg: String?

    /**
     * 操作时间
     */
    val operTime: Date?

    /**
     * 消耗时间
     */
    val costTime: Long?
}
