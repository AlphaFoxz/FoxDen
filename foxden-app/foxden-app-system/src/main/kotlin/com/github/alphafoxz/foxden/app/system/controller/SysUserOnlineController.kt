package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.domain.dto.UserOnlineDTO
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.redis.utils.RedisUtils
import com.github.alphafoxz.foxden.common.web.core.BaseController
import org.springframework.web.bind.annotation.*

/**
 * 在线用户监控
 *
 * @author FoxDen Team
 */
@RestController
@RequestMapping("/monitor/online")
class SysUserOnlineController : BaseController() {

    /**
     * 获取在线用户列表
     */
    @SaCheckPermission("monitor:online:list")
    @GetMapping("/list")
    fun list(pageQuery: PageQuery): TableDataInfo<UserOnlineDTO> {
        // 从Redis获取在线用户列表
        val keys = RedisUtils.keys("*")
        val onlineUsers = mutableListOf<UserOnlineDTO>()

        // 这里应该从Redis中获取所有在线用户
        // 暂时返回空列表
        val pageNum = pageQuery.pageNum ?: 1
        val pageSize = pageQuery.pageSize ?: onlineUsers.size
        return TableDataInfo.build(onlineUsers, pageNum, pageSize)
    }

    /**
     * 强退用户
     */
    @SaCheckPermission("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    fun forceLogout(@PathVariable tokenId: String): R<Void> {
        // TODO: 实现强制退出逻辑
        return R.ok()
    }
}
