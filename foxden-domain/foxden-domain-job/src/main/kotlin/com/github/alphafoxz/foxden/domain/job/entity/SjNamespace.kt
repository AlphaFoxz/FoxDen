package com.github.alphafoxz.foxden.domain.job.entity

import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.babyfish.jimmer.sql.*
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
/**
 * 命名空间
 */
@Entity
@Table(name = "sj_namespace")
interface SjNamespace : CommDelFlag, CommId {
    /** 名称 */
    val name: String

    /** 唯一id */
    val uniqueId: String

    /** 描述 */
    val description: String
}
