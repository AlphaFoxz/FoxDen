package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 岗位信息视图对象 sys_post
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysPostVo(
    /**
     * 岗位序号
     */
    var postId: Long? = null,

    /**
     * 岗位编码
     */
    var postCode: String? = null,

    /**
     * 岗位名称
     */
    var postName: String? = null,

    /**
     * 显示顺序
     */
    var postSort: Int? = null,

    /**
     * 状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 更新时间
     */
    var updateTime: java.time.LocalDateTime? = null,

    /**
     * 更新者
     */
    var updateBy: String? = null
)
