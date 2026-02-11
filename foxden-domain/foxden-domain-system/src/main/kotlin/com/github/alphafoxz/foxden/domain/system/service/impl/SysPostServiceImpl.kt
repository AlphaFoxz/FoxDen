package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysPostBo
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysDeptService
import com.github.alphafoxz.foxden.domain.system.service.SysPostService
import com.github.alphafoxz.foxden.domain.system.vo.SysPostVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

/**
 * Post 业务层处理
 */
@Service
class SysPostServiceImpl(
    private val sqlClient: KSqlClient,
    private val jdbcTemplate: JdbcTemplate,
    private val deptService: SysDeptService
) : SysPostService {

    override fun selectPostList(post: SysPostBo): List<SysPostVo> {
        // 部门树搜索：预先获取部门ID列表
        val deptIdsForTreeSearch = if (post.deptId == null && post.belongDeptId != null) {
            deptService.selectDeptAndChildById(post.belongDeptId!!)
        } else {
            null
        }

        val posts = sqlClient.createQuery(SysPost::class) {
            post.postId?.let { where(table.id eq it) }
            post.postCode?.takeIf { it.isNotBlank() }?.let { where(table.postCode like "%${it}%") }
            post.postCategory?.takeIf { it.isNotBlank() }?.let { where(table.postCategory like "%${it}%") }
            post.postName?.takeIf { it.isNotBlank() }?.let { where(table.postName like "%${it}%") }
            post.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }

            // 部门过滤逻辑：优先单部门搜索
            post.deptId?.let { where(table.deptId eq it) }

            orderBy(table.postSort.asc())
            select(table)
        }.execute()

        // 如果需要部门树搜索，在内存中过滤
        val filteredPosts = if (deptIdsForTreeSearch != null) {
            posts.filter { it.deptId in deptIdsForTreeSearch }
        } else {
            posts
        }

        return filteredPosts.map { entityToVo(it) }
    }

    override fun selectPagePostList(post: SysPostBo, pageQuery: PageQuery): TableDataInfo<SysPostVo> {
        // 部门树搜索：预先获取部门ID列表
        val deptIdsForTreeSearch = if (post.deptId == null && post.belongDeptId != null) {
            deptService.selectDeptAndChildById(post.belongDeptId!!)
        } else {
            null
        }

        // 如果是部门树搜索，使用 JdbcTemplate 实现
        if (deptIdsForTreeSearch != null) {
            return selectPagePostListWithJdbc(post, pageQuery, deptIdsForTreeSearch)
        }

        // 单部门搜索或无部门过滤，使用 Jimmer
        val pager = sqlClient.createQuery(SysPost::class) {
            post.postCode?.takeIf { it.isNotBlank() }?.let { where(table.postCode like "%${it}%") }
            post.postCategory?.takeIf { it.isNotBlank() }?.let { where(table.postCategory like "%${it}%") }
            post.postName?.takeIf { it.isNotBlank() }?.let { where(table.postName like "%${it}%") }
            post.status?.takeIf { it.isNotBlank() }?.let { where(table.status eq it) }

            // 部门过滤：单部门搜索
            post.deptId?.let { where(table.deptId eq it) }

            orderBy(table.postSort.asc())
            select(table)
        }.fetchPage((pageQuery.pageNum ?: 1) - 1, pageQuery.pageSize ?: 10)

        return TableDataInfo(pager.rows.map { entityToVo(it) }, pager.totalRowCount)
    }

    /**
     * 使用 JdbcTemplate 实现带部门树搜索的分页查询
     */
    private fun selectPagePostListWithJdbc(
        post: SysPostBo,
        pageQuery: PageQuery,
        deptIds: List<Long>
    ): TableDataInfo<SysPostVo> {
        // 构建 IN 子句
        val inClause = deptIds.joinToString(",") { "?" }

        // 构建动态 SQL 条件
        val conditions = mutableListOf<String>()
        val params = mutableListOf<Any>()

        post.postCode?.takeIf { it.isNotBlank() }?.let {
            conditions.add("post_code LIKE ?")
            params.add("%$it%")
        }
        post.postCategory?.takeIf { it.isNotBlank() }?.let {
            conditions.add("post_category LIKE ?")
            params.add("%$it%")
        }
        post.postName?.takeIf { it.isNotBlank() }?.let {
            conditions.add("post_name LIKE ?")
            params.add("%$it%")
        }
        post.status?.takeIf { it.isNotBlank() }?.let {
            conditions.add("status = ?")
            params.add(it)
        }

        // 部门条件
        conditions.add("dept_id IN ($inClause)")
        params.addAll(deptIds)

        val whereClause = if (conditions.isNotEmpty()) {
            "WHERE " + conditions.joinToString(" AND ")
        } else {
            ""
        }

        // 查询总数
        val countSql = "SELECT COUNT(*) FROM sys_post $whereClause"
        val total = jdbcTemplate.queryForObject(countSql, Long::class.java, *params.toTypedArray()) ?: 0

        // 查询分页数据
        val offset = ((pageQuery.pageNum ?: 1) - 1) * (pageQuery.pageSize ?: 10)
        val limit = pageQuery.pageSize ?: 10
        val dataSql = """
            SELECT post_id, post_code, post_name, post_category, post_sort,
                   status, remark, create_time, update_time, dept_id
            FROM sys_post
            $whereClause
            ORDER BY post_sort ASC
            LIMIT ? OFFSET ?
        """.trimIndent()

        val queryParams = params.toMutableList()
        queryParams.add(limit)
        queryParams.add(offset)

        val rows = jdbcTemplate.query(dataSql, queryParams.toTypedArray()) { rs, _ ->
            SysPostVo(
                postId = rs.getLong("post_id"),
                deptId = rs.getLong("dept_id").takeIf { !rs.wasNull() },
                postCode = rs.getString("post_code"),
                postName = rs.getString("post_name"),
                postCategory = rs.getString("post_category"),
                postSort = rs.getInt("post_sort"),
                status = rs.getString("status"),
                remark = rs.getString("remark"),
                createTime = rs.getTimestamp("create_time")?.toLocalDateTime(),
                updateTime = rs.getTimestamp("update_time")?.toLocalDateTime(),
                createBy = null,
                updateBy = null,
                deptName = null
            )
        }

        return TableDataInfo(rows, total)
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
        val vo = entityToVo(post)

        // 单独查询部门名称（避免懒加载关联导致 UnloadedException）
        if (post.deptId != null) {
            val dept = deptService.selectDeptById(post.deptId!!)
            vo.deptName = dept?.deptName
        }

        return vo
    }

    override fun selectPostsByUserId(userId: Long): List<Long> {
        // Query the join table directly to get post IDs for user
        return jdbcTemplate.queryForList(
            "SELECT post_id FROM sys_user_post WHERE user_id = ?",
            Long::class.java,
            userId
        )
    }

    override fun selectPostListByUserId(userId: Long): List<Long> {
        return selectPostsByUserId(userId)
    }

    override fun selectPostByIds(postIds: List<Long>): List<SysPostVo> {
        if (postIds.isEmpty()) {
            return emptyList()
        }

        // 使用 JdbcTemplate 进行 IN 查询（Jimmer DSL 的 in 操作符有语法问题）
        val inClause = postIds.joinToString(",") { "?" }
        val sql = """
            SELECT post_id, post_code, post_name, post_category, post_sort,
                   status, remark, create_time, update_time, dept_id
            FROM sys_post
            WHERE post_id IN ($inClause)
            AND status = ?
            ORDER BY post_sort ASC
        """.trimIndent()

        val params = mutableListOf<Any>()
        params.addAll(postIds)
        params.add(SystemConstants.NORMAL)

        return jdbcTemplate.query(sql, params.toTypedArray()) { rs, _ ->
            SysPostVo(
                postId = rs.getLong("post_id"),
                deptId = rs.getLong("dept_id").takeIf { !rs.wasNull() },
                postCode = rs.getString("post_code"),
                postName = rs.getString("post_name"),
                postCategory = rs.getString("post_category"),
                postSort = rs.getInt("post_sort"),
                status = rs.getString("status"),
                remark = rs.getString("remark"),
                createTime = rs.getTimestamp("create_time")?.toLocalDateTime(),
                updateTime = rs.getTimestamp("update_time")?.toLocalDateTime(),
                createBy = null,
                updateBy = null,
                deptName = null
            )
        }
    }

    override fun deletePostByIds(postIds: Array<Long>) {
        // 参照老系统：删除前检查是否已分配用户
        for (postId in postIds) {
            if (countUserPostById(postId) > 0) {
                val post = sqlClient.findById(SysPost::class, postId)
                throw ServiceException("岗位'${post?.postName}'已分配，不能删除!")
            }
        }

        sqlClient.deleteByIds(SysPost::class, postIds.toList())
    }

    override fun countUserPostById(postId: Long): Long {
        val count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM sys_user_post WHERE post_id = ?",
            Long::class.java,
            postId
        )
        return count ?: 0
    }

    override fun countPostByDeptId(deptId: Long): Long {
        return sqlClient.createQuery(SysPost::class) {
            where(table.deptId eq deptId)
            select(table.id)
        }.execute().count().toLong()
    }

    override fun selectPostNamesByIds(postIds: List<Long>): Map<Long, String> {
        if (postIds.isEmpty()) {
            return emptyMap()
        }

        // 使用 JdbcTemplate 查询岗位ID和名称映射（参照老系统实现）
        val inClause = postIds.joinToString(",") { "?" }
        val sql = "SELECT post_id, post_name FROM sys_post WHERE post_id IN ($inClause)"

        return jdbcTemplate.query(sql, postIds.toTypedArray()) { rs, _ ->
            rs.getLong("post_id") to rs.getString("post_name")
        }.toMap()
    }

    override fun insertPost(post: SysPostBo): Int {
        val newPost = com.github.alphafoxz.foxden.domain.system.entity.SysPostDraft.`$`.produce {
            postCode = post.postCode ?: throw ServiceException("岗位编码不能为空")
            postName = post.postName ?: throw ServiceException("岗位名称不能为空")
            postSort = post.postSort ?: 0
            status = post.status ?: SystemConstants.NORMAL
            postCategory = post.postCategory
            remark = post.remark
            createTime = java.time.LocalDateTime.now()
        }

        // 使用 INSERT_ONLY 模式，明确告诉 Jimmer 这是插入操作
        // 因为 SysPost 的 id 是自动生成的，在 Draft 中为 null，Jimmer 无法自动判断
        val result = sqlClient.save(newPost) {
            setMode(org.babyfish.jimmer.sql.ast.mutation.SaveMode.INSERT_ONLY)
        }
        return if (result.isModified) 1 else 0
    }

    override fun updatePost(post: SysPostBo): Int {
        val postIdVal = post.postId ?: return 0

        val result = sqlClient.createUpdate(SysPost::class) {
            where(table.id eq postIdVal)
            post.postCode?.let { set(table.postCode, it) }
            post.postName?.let { set(table.postName, it) }
            post.postSort?.let { set(table.postSort, it) }
            post.postCategory?.let { set(table.postCategory, it) }
            post.status?.let { set(table.status, it) }
            post.remark?.let { set(table.remark, it) }
            set(table.updateTime, java.time.LocalDateTime.now())
        }.execute()
        return result
    }

    override fun checkPostNameUnique(post: SysPostBo): Boolean {
        val existing = sqlClient.createQuery(SysPost::class) {
            where(table.postName eq post.postName)
            where(table.deptId eq (post.deptId ?: 0L))
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
            deptId = post.deptId,
            postCode = post.postCode,
            postName = post.postName,
            postCategory = post.postCategory,
            postSort = post.postSort,
            status = post.status,
            remark = post.remark,
            createTime = post.createTime,
            updateTime = post.updateTime,
            // Note: createBy/updateBy in entity are Long (user ID), but VO expects String (username)
            // For now, skip setting these fields
            createBy = null,
            updateBy = null,
            // deptName 由调用方单独查询，避免懒加载关联导致 UnloadedException
            deptName = null
        )
    }
}
