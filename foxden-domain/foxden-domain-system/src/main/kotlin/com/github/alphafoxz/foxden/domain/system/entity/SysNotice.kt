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
 * 通知公告
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_notice")
interface SysNotice : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 公告标题
     */
    @Column(name = "notice_title")
    @get:Length(max = 2147483647)
    val noticeTitle: String

    /**
     * 公告类型（1通知 2公告）
     */
    @Column(name = "notice_type")
    @get:Length(max = 1)
    val noticeType: String

    /**
     * 公告内容
     */
    @Column(name = "notice_content")
    @get:Length(max = 2147483647)
    val noticeContent: String?

    /**
     * 公告状态（0正常 1关闭）
     */
    @Column(name = "status")
    @get:Length(max = 1)
    val status: String

    /**
     * 备注
     */
    @Column(name = "remark")
    @get:Length(max = 2147483647)
    val remark: String?
}
