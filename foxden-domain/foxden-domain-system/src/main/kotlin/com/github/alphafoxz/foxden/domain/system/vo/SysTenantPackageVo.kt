package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 租户套餐视图对象 sys_tenant_package
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysTenantPackageVo(
    /**
     * 套餐ID
     */
    var packageId: Long? = null,

    /**
     * 套餐名称
     */
    var packageName: String? = null,

    /**
     * 关联菜单ID
     */
    var menuIds: String? = null,

    /**
     * 关联菜单名称
     */
    var menuCheckStrictly: Boolean? = null,

    /**
     * 菜单树
     */
    var menus: List<String>? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 套餐状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
