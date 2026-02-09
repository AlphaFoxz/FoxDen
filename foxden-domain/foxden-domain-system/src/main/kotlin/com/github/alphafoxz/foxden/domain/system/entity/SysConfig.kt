package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 参数配置表 sys_config
 */
@Entity
@Table(name = "sys_config")
interface SysConfig : CommInfo, CommTenant {
    /**
     * 主键ID
     */
    @org.babyfish.jimmer.sql.Column(name = "config_id")
    @Id
    @GeneratedValue
    val id: Long

    /**
     * 参数名称
     */
    val configName: String

    /**
     * 参数键名
     */
    val configKey: String

    /**
     * 参数键值
     */
    val configValue: String?

    /**
     * 系统内置（Y是 N否）
     */
    val configType: String?

    /**
     * 备注
     */
    val remark: String?
}
