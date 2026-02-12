package com.github.alphafoxz.foxden.app.system.controller

import cn.dev33.satoken.annotation.SaCheckPermission
import cn.dev33.satoken.exception.NotLoginException
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.StrUtil
import com.github.alphafoxz.foxden.common.core.constant.CacheConstants
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.domain.dto.UserOnlineDTO
import com.github.alphafoxz.foxden.common.core.utils.StreamUtils
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
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
    fun list(
        @RequestParam(required = false) ipaddr: String?,
        @RequestParam(required = false) userName: String?,
        pageQuery: PageQuery
    ): TableDataInfo<UserOnlineDTO> {
        // 获取所有未过期的 token
        val keys = RedisUtils.keys(CacheConstants.ONLINE_TOKEN_KEY + "*")
        val userOnlineDTOList = mutableListOf<UserOnlineDTO>()

        for (key in keys) {
            val token = org.apache.commons.lang3.StringUtils.substringAfterLast(key, ":").toString()
            // 如果已经过期则跳过
            if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) < -1) {
                continue
            }
            val userOnlineDTO = RedisUtils.getCacheObject<UserOnlineDTO>(CacheConstants.ONLINE_TOKEN_KEY + token)
            userOnlineDTO?.let { userOnlineDTOList.add(it) }
        }

        // 根据条件过滤
        val filteredList = when {
            ipaddr.isNullOrBlank() && userName.isNullOrBlank() -> userOnlineDTOList
            ipaddr.isNullOrBlank() -> StreamUtils.filter(userOnlineDTOList) {
                StrUtil.equalsAnyIgnoreCase(userName, it.userName)
            }

            userName.isNullOrBlank() -> StreamUtils.filter(userOnlineDTOList) {
                StrUtil.equalsAnyIgnoreCase(ipaddr, it.ipaddr)
            }

            else -> StreamUtils.filter(userOnlineDTOList) {
                StrUtil.equalsAnyIgnoreCase(ipaddr, it.ipaddr) && StrUtil.equalsAnyIgnoreCase(userName, it.userName)
            }
        }

        // 反转列表
        val reversedList = filteredList.reversed()
        val listWithoutNull = reversedList.filterNotNull()

        val pageNum = pageQuery.pageNum ?: 1
        val pageSize = pageQuery.pageSize ?: listWithoutNull.size
        val startIndex = (pageNum - 1) * pageSize
        val endIndex = minOf(startIndex + pageSize, listWithoutNull.size)

        val pageList = if (listWithoutNull.isNotEmpty() && startIndex < listWithoutNull.size) {
            listWithoutNull.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return TableDataInfo.build(pageList, pageNum, pageSize)
    }

    /**
     * 强退用户
     */
    @SaCheckPermission("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @RepeatSubmit
    @DeleteMapping("/{tokenId}")
    fun forceLogout(@PathVariable tokenId: String): R<Void> {
        try {
            StpUtil.kickoutByTokenValue(tokenId)
        } catch (ignored: NotLoginException) {
            // Token已过期，忽略
        }
        return R.ok()
    }
}
