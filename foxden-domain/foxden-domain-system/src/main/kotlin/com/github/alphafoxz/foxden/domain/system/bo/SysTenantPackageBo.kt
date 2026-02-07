package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * 租户套餐业务对象 sys_tenant_package
 *
 * @author Lion Li
 */
data class SysTenantPackageBo(
    /**
     * 套餐ID
     */
    var packageId: Long? = null,

    /**
     * 套餐名称
     */
    @get:NotBlank(message = "套餐名称不能为空")
    @get:Size(min = 0, max = 20, message = "套餐名称长度不能超过{max}个字符")
    var packageName: String? = null,

    /**
     * 关联菜单ID
     */
    @get:NotNull(message = "关联菜单不能为空")
    var menuIds: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 套餐状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
