package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 字典数据表 sys_dict_data
 */
@Entity
interface SysDictData : CommId, CommInfo, CommTenant {
    /**
     * 字典排序
     */
    val dictSort: Int?

    /**
     * 字典标签
     */
    val dictLabel: String

    /**
     * 字典键值
     */
    val dictValue: String?

    /**
     * 字典类型
     */
    val dictType: String

    /**
     * 样式属性（其他样式扩展）
     */
    val cssClass: String?

    /**
     * 表格字典样式
     */
    val listClass: String?

    /**
     * 是否默认（Y是 N否）
     */
    val isDefault: String?

    /**
     * 字典类型
     */
    @ManyToOne
    @JoinColumn(name = "dict_type")
    val dictTypeObj: SysDictType?

    /**
     * 是否默认值
     */
    fun getDefault(): Boolean {
        return SystemConstants.YES == isDefault
    }
}
