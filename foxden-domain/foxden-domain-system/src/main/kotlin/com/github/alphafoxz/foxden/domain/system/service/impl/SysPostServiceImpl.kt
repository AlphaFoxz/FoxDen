package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysPostService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.domain.system.bo.SysPostBo
import com.github.alphafoxz.foxden.domain.system.vo.SysPostVo

/**
 * Post 业务层处理
 */
@Service
class SysPostServiceImpl(): SysPostService {

    override fun selectPostList(post: SysPostBo): List<SysPostVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectPostAll(): List<SysPostVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectPostById(postId: Long): SysPostVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun selectPostsByUserId(userId: Long): List<Long> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun deletePostByIds(postIds: Array<Long>) {
        // TODO: 实现业务逻辑
    }

    override fun insertPost(post: SysPostBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updatePost(post: SysPostBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun checkPostNameUnique(post: SysPostBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun checkPostCodeUnique(post: SysPostBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }
}
