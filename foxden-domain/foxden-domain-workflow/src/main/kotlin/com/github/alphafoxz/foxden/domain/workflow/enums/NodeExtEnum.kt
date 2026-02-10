package com.github.alphafoxz.foxden.domain.workflow.enums

/**
 * 节点扩展属性枚举接口
 *
 * @author AprilWind
 */
interface NodeExtEnum {
    /**
     * 选项label
     */
    fun getLabel(): String

    /**
     * 选项值
     */
    fun getValue(): String

    /**
     * 是否默认选中
     */
    fun isSelected(): Boolean
}
