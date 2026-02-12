package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommInfo
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.jackson.LongToStringConverter
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.Table

/**
 * 字典数据表 sys_dict_data
 */
@Entity
@Table(name = "sys_dict_data")
interface SysDictData : CommInfo, CommTenant {
    /**
     * 主键ID
     */
    @Column(name = "dict_code")
    @Id
    @JsonConverter(LongToStringConverter::class)
    val id: Long

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
    @Column(name = "is_default")
    val defaultFlag: String?

    /**
     * 备注
     */
    val remark: String?

    /**
     * 是否默认值
     */
    fun getDefault(): Boolean {
        return SystemConstants.YES == defaultFlag
    }
}
