package com.github.alphafoxz.foxden.domain.workflow.vo

import java.io.Serializable

/**
 * 按钮权限视图对象
 *
 * @author AprilWind
 */
data class ButtonPermissionVo(
    /**
     * 唯一编码
     */
    var code: String? = null,

    /**
     * 选项值
     */
    var value: String? = null,

    /**
     * 是否显示
     */
    var show: Boolean? = null
) : Serializable {

    constructor(code: String, show: Boolean) : this(code, null, show)

    companion object {
        /**
         * 创建一个空的按钮权限对象
         */
        fun empty(): ButtonPermissionVo = ButtonPermissionVo()
    }
}
