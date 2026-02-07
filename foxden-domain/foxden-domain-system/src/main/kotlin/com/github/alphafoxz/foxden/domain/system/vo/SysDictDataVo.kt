package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 字典数据视图对象 sys_dict_data
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysDictDataVo(
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
    var dictLabel: String? = null,

    /**
     * 键值
     */
    var dictValue: String? = null,

    /**
     * 字典类型
     */
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
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
