package com.github.alphafoxz.foxden.common.jimmer.entity.comm

import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.GeneratedValue
import org.babyfish.jimmer.sql.GenerationType
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.MappedSuperclass

/**
 * @author wong
 */
@MappedSuperclass
interface CommId {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long
}
