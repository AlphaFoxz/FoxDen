package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 字典类型表 sys_dict_type
 */
@Entity
interface SysDictType : CommId, CommInfo, CommTenant {
    /**
     * 字典名称
     */
    val dictName: String

    /**
     * 字典类型
     */
    val dictType: String

    /**
     * 字典数据
     */
    @OnDissociate(DissociateAction.DELETE)
    @OneToMany(mappedBy = "dictType")
    val dictData: List<SysDictData>
}
