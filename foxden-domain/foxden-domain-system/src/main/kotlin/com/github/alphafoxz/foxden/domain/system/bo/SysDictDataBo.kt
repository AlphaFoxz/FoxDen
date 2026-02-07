package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 字典数据业务对象 sys_dict_data
 *
 * @author Lion Li
 */
data class SysDictDataBo(
    /**
     * 字典编码
     */
    var dictCode: Long? = null,

    /**
     * 顺序
     */
    var dictSort: Int? = null,

    /**
     * 字典标签
     */
    @get:NotBlank(message = "字典标签不能为空")
    @get:Size(min = 0, max = 100, message = "字典标签长度不能超过{max}个字符")
    var dictLabel: String? = null,

    /**
     * 键值
     */
    @get:NotBlank(message = "字典键值不能为空")
    @get:Size(min = 0, max = 100, message = "字典键值长度不能超过{max}个字符")
    var dictValue: String? = null,

    /**
     * 字典类型
     */
    @get:NotBlank(message = "字典类型不能为空")
    @get:Size(min = 0, max = 100, message = "字典类型长度不能超过{max}个字符")
    var dictType: String? = null,

    /**
     * 样式属性（其他样式扩展）
     */
    var cssClass: String? = null,

    /**
     * 表格回显样式
     */
    var listClass: String? = null,

    /**
     * 是否默认（0是 1否）
     */
    var isDefault: String? = null,

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
