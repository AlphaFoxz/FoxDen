package com.github.alphafoxz.foxden.domain.workflow.enums

/**
 * 抄送设置枚举
 *
 * @author AprilWind
 */
enum class CopySettingEnum(
    private val label: String,
    private val value: String,
    private val selected: Boolean
) : NodeExtEnum {

    /** 并行会签 */
    PARALLEL("并行会签", "parallel", true),

    /** 串行会签 */
    SEQUENTIAL("串行会签", "sequential", false),

    /** 独占 */
    EXCLUSIVE("独占", "exclusive", false);

    override fun getLabel(): String = label
    override fun getValue(): String = value
    override fun isSelected(): Boolean = selected
}
