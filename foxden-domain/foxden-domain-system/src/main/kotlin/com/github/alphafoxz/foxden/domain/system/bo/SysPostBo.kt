package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * 岗位信息业务对象 sys_post
 *
 * @author Lion Li
 */
data class SysPostBo(
    /**
     * 岗位序号
     */
    var postId: Long? = null,

    /**
     * 部门ID
     */
    var deptId: Long? = null,

    /**
     * 岗位编码
     */
    @get:NotBlank(message = "岗位编码不能为空")
    @get:Size(min = 0, max = 64, message = "岗位编码长度不能超过{max}个字符")
    var postCode: String? = null,

    /**
     * 岗位名称
     */
    @get:NotBlank(message = "岗位名称不能为空")
    @get:Size(min = 0, max = 50, message = "岗位名称长度不能超过{max}个字符")
    var postName: String? = null,

    /**
     * 显示顺序
     */
    @get:NotNull(message = "显示顺序不能为空")
    var postSort: Int? = null,

    /**
     * 状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
