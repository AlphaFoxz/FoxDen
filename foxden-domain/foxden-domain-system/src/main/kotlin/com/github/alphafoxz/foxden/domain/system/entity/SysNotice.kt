package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 通知公告表 sys_notice
 */
@Entity
@Table(name = "sys_notice")
interface SysNotice : CommInfo, CommTenant {
    /**
     * 主键ID
     */
    @org.babyfish.jimmer.sql.Column(name = "notice_id")
    @Id
    @GeneratedValue
    val id: Long

    /**
     * 公告标题
     */
    val noticeTitle: String

    /**
     * 公告类型（1通知 2公告）
     */
    val noticeType: String?

    /**
     * 公告内容
     */
    val noticeContent: String?

    /**
     * 公告状态（0正常 1关闭）
     */
    val status: String?

    /**
     * 备注
     */
    val remark: String?
}
