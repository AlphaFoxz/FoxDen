package com.github.alphafoxz.foxden.domain.system.bo

import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * 部门业务对象 sys_dept
 *
 * @author Michelle.Chung
 */
data class SysDeptBo(
    /**
     * 部门id
     */
    var deptId: Long? = null,

    /**
     * 父部门ID
     */
    var parentId: Long? = null,

    /**
     * 部门名称
     */
    @get:NotBlank(message = "部门名称不能为空", groups = [AddGroup::class, EditGroup::class])
    @get:Size(min = 0, max = 30, message = "部门名称长度不能超过{max}个字符")
    var deptName: String? = null,

    /**
     * 部门类别编码
     */
    @get:Size(min = 0, max = 100, message = "部门类别编码长度不能超过{max}个字符")
    var deptCategory: String? = null,

    /**
     * 显示顺序
     */
    @get:NotNull(message = "显示顺序不能为空", groups = [AddGroup::class, EditGroup::class])
    var orderNum: Int? = null,

    /**
     * 负责人
     */
    var leader: Long? = null,

    /**
     * 联系电话
     */
    @get:Size(min = 0, max = 11, message = "联系电话长度不能超过{max}个字符")
    var phone: String? = null,

    /**
     * 邮箱
     */
    @get:Email(message = "邮箱格式不正确")
    @get:Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符")
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
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null,

    /**
     * 更新者
     */
    var updateBy: String? = null,

    /**
     * 更新时间
     */
    var updateTime: java.time.LocalDateTime? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SysDeptBo) return false
        return deptId == other.deptId
    }

    override fun hashCode(): Int {
        return deptId?.hashCode() ?: 0
    }
}
