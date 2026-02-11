package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.domain.system.bo.SysPostBo
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysPostService
import com.github.alphafoxz.foxden.domain.system.vo.SysPostVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.like
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

/**
 * Post 业务层处理
 */
@Service
class SysPostServiceImpl(
    private val sqlClient: KSqlClient,
    private val jdbcTemplate: JdbcTemplate
) : SysPostService {

    override fun selectPostList(post: SysPostBo): List<SysPostVo> {
        val posts = sqlClient.createQuery(SysPost::class) {
            post.postId?.let { where(table.id eq it) }
            post.postCode?.takeIf { it.isNotBlank() }?.let { where(table.postCode like "%${it}%") }
            post.postName?.takeIf { it.isNotBlank() }?.let { where(table.postName like "%${it}%") }
            post.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }
            orderBy(table.postSort.asc())
            select(table)
        }.execute()

        return posts.map { entityToVo(it) }
    }

    override fun selectPostAll(): List<SysPostVo> {
        val posts = sqlClient.createQuery(SysPost::class) {
            orderBy(table.postSort.asc())
            select(table)
        }.execute()

        return posts.map { entityToVo(it) }
    }

    override fun selectPostById(postId: Long): SysPostVo? {
        val post = sqlClient.findById(SysPost::class, postId) ?: return null
        return entityToVo(post)
    }

    override fun selectPostsByUserId(userId: Long): List<Long> {
        // Query the join table directly to get post IDs for the user
        return jdbcTemplate.queryForList(
            "SELECT post_id FROM sys_user_post WHERE user_id = ?",
            Long::class.java,
            userId
        )
    }

    override fun deletePostByIds(postIds: Array<Long>) {
        sqlClient.deleteByIds(SysPost::class, postIds.toList())
    }

    override fun insertPost(post: SysPostBo): Int {
        val newPost = com.github.alphafoxz.foxden.domain.system.entity.SysPostDraft.`$`.produce {
            postCode = post.postCode ?: throw ServiceException("岗位编码不能为空")
            postName = post.postName ?: throw ServiceException("岗位名称不能为空")
            postSort = post.postSort ?: 0
            status = post.status ?: SystemConstants.NORMAL
            remark = post.remark
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(newPost)
        return if (result.isModified) 1 else 0
    }

    override fun updatePost(post: SysPostBo): Int {
        val postIdVal = post.postId ?: return 0

        val result = sqlClient.createUpdate(SysPost::class) {
            where(table.id eq postIdVal)
            post.postCode?.let { set(table.postCode, it) }
            post.postName?.let { set(table.postName, it) }
            post.postSort?.let { set(table.postSort, it) }
            post.status?.let { set(table.status, it) }
            post.remark?.let { set(table.remark, it) }
            set(table.updateTime, java.time.LocalDateTime.now())
        }.execute()
        return result
    }

    override fun checkPostNameUnique(post: SysPostBo): Boolean {
        val existing = sqlClient.createQuery(SysPost::class) {
            where(table.postName eq post.postName)
            post.postId?.let { where(table.id ne it) }
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == post.postId
    }

    override fun checkPostCodeUnique(post: SysPostBo): Boolean {
        val existing = sqlClient.createQuery(SysPost::class) {
            where(table.postCode eq post.postCode)
            post.postId?.let { where(table.id ne it) }
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == post.postId
    }

    /**
     * 实体转 VO
     */
    private fun entityToVo(post: SysPost): SysPostVo {
        return SysPostVo(
            postId = post.id,
            postCode = post.postCode,
            postName = post.postName,
            postSort = post.postSort,
            status = post.status,
            remark = post.remark,
            createTime = post.createTime,
            updateTime = post.updateTime
            // Note: createBy/updateBy in entity are Long (user ID), but VO expects String (username)
            // For now, skip setting these fields
        )
    }
}
