package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysPostBo
import com.github.alphafoxz.foxden.domain.system.vo.SysPostVo

/**
 * 岗位信息 服务层
 *
 * @author Lion Li
 */
interface SysPostService {

    /**
     * 查询岗位列表
     *
     * @param post 岗位信息
     * @return 岗位列表
     */
    fun selectPostList(post: SysPostBo): List<SysPostVo>

    /**
     * 分页查询岗位列表
     *
     * @param post 岗位信息
     * @param pageQuery 分页参数
     * @return 岗位分页列表
     */
    fun selectPagePostList(post: SysPostBo, pageQuery: PageQuery): TableDataInfo<SysPostVo>

    /**
     * 查询所有岗位
     *
     * @return 岗位列表
     */
    fun selectPostAll(): List<SysPostVo>

    /**
     * 根据岗位ID查询信息
     *
     * @param postId 岗位ID
     * @return 岗位信息
     */
    fun selectPostById(postId: Long): SysPostVo?

    /**
     * 根据用户ID查询岗位ID列表
     *
     * @param userId 用户ID
     * @return 岗位ID列表
     */
    fun selectPostsByUserId(userId: Long): List<Long>

    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    fun selectPostListByUserId(userId: Long): List<Long>

    /**
     * 通过岗位ID串查询岗位
     *
     * @param postIds 岗位id串
     * @return 岗位列表信息
     */
    fun selectPostByIds(postIds: List<Long>): List<SysPostVo>

    /**
     * 批量删除岗位信息
     *
     * @param postIds 需要删除的岗位ID
     */
    fun deletePostByIds(postIds: Array<Long>)

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     * @return 结果
     */
    fun countUserPostById(postId: Long): Long

    /**
     * 通过部门ID查询岗位使用数量
     *
     * @param deptId 部门id
     * @return 结果
     */
    fun countPostByDeptId(deptId: Long): Long

    /**
     * 根据岗位 ID 列表查询岗位名称映射关系
     *
     * @param postIds 岗位 ID 列表
     * @return Map，其中 key 为岗位 ID，value 为对应的岗位名称
     */
    fun selectPostNamesByIds(postIds: List<Long>): Map<Long, String>

    /**
     * 新增岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    fun insertPost(post: SysPostBo): Int

    /**
     * 修改岗位信息
     *
     * @param post 岗位信息
     * @return 结果
     */
    fun updatePost(post: SysPostBo): Int

    /**
     * 校验岗位名称
     *
     * @param post 岗位信息
     * @return 结果
     */
    fun checkPostNameUnique(post: SysPostBo): Boolean

    /**
     * 校验岗位编码
     *
     * @param post 岗位信息
     * @return 结果
     */
    fun checkPostCodeUnique(post: SysPostBo): Boolean
}
