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
 * 参数配置
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_config")
interface SysConfig : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 参数名称
     */
    @Column(name = "config_name")
    @get:Length(max = 2147483647)
    val configName: String

    /**
     * 参数键名
     */
    @Column(name = "config_key")
    @get:Length(max = 2147483647)
    val configKey: String

    /**
     * 参数键值
     */
    @Column(name = "config_value")
    @get:Length(max = 2147483647)
    val configValue: String?

    /**
     * 系统内置（Y是 N否）
     */
    @Column(name = "config_type")
    @get:Length(max = 1)
    val configType: String?

    /**
     * 备注
     */
    @Column(name = "remark")
    @get:Length(max = 2147483647)
    val remark: String?
}
