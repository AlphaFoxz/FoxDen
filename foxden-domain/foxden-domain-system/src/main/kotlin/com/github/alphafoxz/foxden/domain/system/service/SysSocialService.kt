package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.domain.system.bo.SysSocialBo
import com.github.alphafoxz.foxden.domain.system.vo.SysSocialVo

/**
 * 社会化关系 服务层
 *
 * @author Lion Li
 */
interface SysSocialService {

    /**
     * 查询社会化关系列表
     *
     * @param bo 社会化关系信息
     * @return 社会化关系集合
     */
    fun selectSocialList(bo: SysSocialBo): List<SysSocialVo>

    /**
     * 根据SocialId查询社会化关系
     *
     * @param socialId 社会化关系ID
     * @return 社会化关系
     */
    fun selectSocialById(socialId: Long): SysSocialVo?

    /**
     * 根据认证ID查询社会化关系列表
     *
     * @param authId 认证ID
     * @return 社会化关系集合
     */
    fun selectByAuthId(authId: String): List<SysSocialVo>

    /**
     * 新增社会化关系
     *
     * @param bo 社会化关系信息
     * @return 结果
     */
    fun insertSocial(bo: SysSocialBo): Int

    /**
     * 修改社会化关系
     *
     * @param bo 社会化关系信息
     * @return 结果
     */
    fun updateSocial(bo: SysSocialBo): Int

    /**
     * 删除社会化关系
     *
     * @param socialId 社会化关系ID
     */
    fun deleteSocialById(socialId: Long)
}
