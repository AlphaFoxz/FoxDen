package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 部门视图对象 sys_dept
 *
 * @author Michelle.Chung
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysDeptVo(
    /**
     * 部门id
     */
    var deptId: Long? = null,

    /**
     * 父部门ID
     */
    var parentId: Long? = null,

    /**
     * 祖级列表
     */
    var ancestors: String? = null,

    /**
     * 部门名称
     */
    var deptName: String? = null,

    /**
     * 部门类别编码
     */
    var deptCategory: String? = null,

    /**
     * 显示顺序
     */
    var orderNum: Int? = null,

    /**
     * 负责人
     */
    var leader: Long? = null,

    /**
     * 负责人姓名
     */
    var leaderName: String? = null,

    /**
     * 联系电话
     */
    var phone: String? = null,

    /**
     * 邮箱
     */
    var email: String? = null,

    /**
     * 部门状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    var delFlag: String? = null,

    /**
     * 父部门名称
     */
    var parentName: String? = null,

    /**
     * 子部门
     */
    var children: List<SysDeptVo>? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
