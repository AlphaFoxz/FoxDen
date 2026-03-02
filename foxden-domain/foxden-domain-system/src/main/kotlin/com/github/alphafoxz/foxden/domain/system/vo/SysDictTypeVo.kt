package com.github.alphafoxz.foxden.domain.system.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 字典类型视图对象 sys_dict_type
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ExcelIgnoreUnannotated
data class SysDictTypeVo(
    /**
     * 字典主键
     */
    @ExcelProperty(value = ["字典主键"])
    var dictId: Long? = null,

    /**
     * 字典名称
     */
    @ExcelProperty(value = ["字典名称"])
    var dictName: String? = null,

    /**
     * 字典类型
     */
    @ExcelProperty(value = ["字典类型"])
    var dictType: String? = null,

    /**
     * 状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 备注
     */
    @ExcelProperty(value = ["备注"])
    var remark: String? = null,

    /**
     * 创建时间
     */
    @ExcelProperty(value = ["创建时间"])
    var createTime: java.time.LocalDateTime? = null
)
