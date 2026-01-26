package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommInfo
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.hibernate.validator.constraints.Length

/**
 * 字典类型
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_dict_type")
interface SysDictType : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 字典名称
     */
    @Column(name = "dict_name")
    @get:Length(max = 2147483647)
    val dictName: String

    /**
     * 字典类型
     */
    @Column(name = "dict_type")
    @get:Length(max = 2147483647)
    val dictType: String

    /**
     * 备注
     */
    @Column(name = "remark")
    @get:Length(max = 2147483647)
    val remark: String?
}
