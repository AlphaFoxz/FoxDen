package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommInfo
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.hibernate.validator.constraints.Length

/**
 * 字典数据
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_dict_data")
interface SysDictData : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 字典排序
     */
    @Column(name = "dict_sort")
    @get:Max(value = 2147483647, message = "字典排序不可大于2147483647")
    @get:Min(value = 0, message = "字典排序不可小于0")
    val dictSort: Int?

    /**
     * 字典标签
     */
    @Column(name = "dict_label")
    @get:Length(max = 2147483647)
    val dictLabel: String

    /**
     * 字典键值
     */
    @Column(name = "dict_value")
    @get:Length(max = 2147483647)
    val dictValue: String

    /**
     * 字典类型
     */
    @Column(name = "dict_type")
    @get:Length(max = 2147483647)
    val dictType: String

    /**
     * 样式属性（其他样式扩展）
     */
    @Column(name = "css_class")
    @get:Length(max = 2147483647)
    val cssClass: String?

    /**
     * 表格回显样式
     */
    @Column(name = "list_class")
    @get:Length(max = 2147483647)
    val listClass: String?

    /**
     * 是否默认
     */
    @Column(name = "default")
    val default: Boolean?

    /**
     * 备注
     */
    @Column(name = "remark")
    @get:Length(max = 2147483647)
    val remark: String?
}
