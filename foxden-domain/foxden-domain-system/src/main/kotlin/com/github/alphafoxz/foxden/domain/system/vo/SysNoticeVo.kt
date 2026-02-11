package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 通知公告视图对象 sys_notice
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysNoticeVo(
    /**
     * 公告ID
     */
    var noticeId: Long? = null,

    /**
     * 公告标题
     */
    var noticeTitle: String? = null,

    /**
     * 公告类型（1通知 2公告）
     */
    var noticeType: String? = null,

    /**
     * 公告内容
     */
    var noticeContent: String? = null,

    /**
     * 状态（0正常 1关闭）
     */
    var status: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 创建人名称
     */
    var createByName: String? = null,

    /**
     * 创建者
     */
    var createBy: Long? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
