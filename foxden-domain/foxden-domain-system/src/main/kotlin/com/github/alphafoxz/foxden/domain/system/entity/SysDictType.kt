package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 字典类型表 sys_dict_type
 */
@Entity
@Table(name = "sys_dict_type")
interface SysDictType : CommInfo, CommTenant {
    /**
     * 主键ID
     */
    @org.babyfish.jimmer.sql.Column(name = "dict_id")
    @Id
    @GeneratedValue
    val id: Long

    /**
     * 字典名称
     */
    val dictName: String

    /**
     * 字典类型
     */
    val dictType: String

    /**
     * 备注
     */
    val remark: String?

    /**
     * 字典数据
     */
    @OneToMany(mappedBy = "dictTypeObj")
    val dictData: List<SysDictData>
}
