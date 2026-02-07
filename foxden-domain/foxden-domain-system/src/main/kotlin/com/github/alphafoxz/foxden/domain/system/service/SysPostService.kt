package com.github.alphafoxz.foxden.domain.system.service

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
     * 根据用户ID查询岗位
     *
     * @param userId 用户ID
     * @return 岗位列表
     */
    fun selectPostsByUserId(userId: Long): List<Long>

    /**
     * 批量删除岗位信息
     *
     * @param postIds 需要删除的岗位ID
     */
    fun deletePostByIds(postIds: Array<Long>)

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
