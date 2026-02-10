package com.github.alphafoxz.foxden.domain.job.entity

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.babyfish.jimmer.sql.*
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
/**
 * 组配置
 */
@Entity
@Table(name = "sj_group_config")
interface SjGroupConfig : CommId {
    /** 命名空间id */
    val namespaceId: String

    /** 组名称 */
    val groupName: String

    /** 组描述 */
    val description: String

    /** token */
    val token: String

    /** 组状态 0、未启用 1、启用 */
    val groupStatus: Int

    /** 版本号 */
    val version: Int

    /** 分区 */
    val groupPartition: Int

    /** 唯一id生成模式 默认号段模式 */
    val idGeneratorMode: Int

    /** 是否初始化场景 0:否 1:是 */
    val initScene: Int

}
