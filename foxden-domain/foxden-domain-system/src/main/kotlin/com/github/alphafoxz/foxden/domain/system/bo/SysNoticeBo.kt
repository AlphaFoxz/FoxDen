package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 通知公告业务对象 sys_notice
 *
 * @author Lion Li
 */
data class SysNoticeBo(
    /**
     * 公告ID
     */
    var noticeId: Long? = null,

    /**
     * 公告标题
     */
    @get:NotBlank(message = "公告标题不能为空")
    @get:Size(min = 0, max = 50, message = "公告标题长度不能超过{max}个字符")
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
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
