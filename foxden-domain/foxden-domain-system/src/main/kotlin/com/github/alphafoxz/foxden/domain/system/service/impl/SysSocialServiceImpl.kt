package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysSocialService
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.bo.SysSocialBo
import com.github.alphafoxz.foxden.domain.system.vo.SysSocialVo
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.*
import org.springframework.stereotype.Service

/**
 * Social 业务层处理
 */
@Service
class SysSocialServiceImpl(
    private val sqlClient: KSqlClient
) : SysSocialService {

    override fun selectSocialList(bo: SysSocialBo): List<SysSocialVo> {
        val socials = sqlClient.createQuery(SysSocial::class) {
            bo.socialId?.let { where(table.id eq it) }
            bo.userId?.let { where(table.userId eq it) }
            bo.type?.takeIf { it.isNotBlank() }?.let { where(table.source eq it) }
            orderBy(table.id.desc())
            select(table)
        }.execute()

        return socials.map { entityToVo(it) }
    }

    override fun selectSocialById(socialId: Long): SysSocialVo? {
        val social = sqlClient.findById(SysSocial::class, socialId) ?: return null
        return entityToVo(social)
    }

    override fun selectByAuthId(authId: String): List<SysSocialVo> {
        val socials = sqlClient.createQuery(SysSocial::class) {
            where(table.authId eq authId)
            orderBy(table.id.desc())
            select(table)
        }.execute()

        return socials.map { entityToVo(it) }
    }

    override fun selectSocialListByUserId(userId: Long): List<SysSocialVo> {
        val socials = sqlClient.createQuery(SysSocial::class) {
            where(table.userId eq userId)
            orderBy(table.id.desc())
            select(table)
        }.execute()

        return socials.map { entityToVo(it) }
    }

    override fun insertSocial(bo: SysSocialBo): Int {
        val newSocial = com.github.alphafoxz.foxden.domain.system.entity.SysSocialDraft.`$`.produce {
            userId = bo.userId
            authId = bo.openid // Map openid to authId
            source = bo.type ?: throw ServiceException("社交平台类型不能为空")
            accessToken = bo.accessToken
            expireIn = bo.expireIn
            refreshToken = bo.refreshToken
            openId = bo.openid
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(newSocial)
        return if (result.isModified) 1 else 0
    }

    override fun updateSocial(bo: SysSocialBo): Int {
        val socialIdVal = bo.socialId ?: return 0
        val existing = sqlClient.findById(SysSocial::class, socialIdVal)
            ?: throw ServiceException("社交关系不存在")

        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysSocialDraft.`$`.produce(existing) {
            bo.userId?.let { userId = it }
            bo.openid?.let {
                authId = it
                openId = it
            }
            bo.type?.let { source = it }
            bo.accessToken?.let { accessToken = it }
            bo.expireIn?.let { expireIn = it }
            bo.refreshToken?.let { refreshToken = it }
            updateTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(updated)
        return if (result.isModified) 1 else 0
    }

    override fun deleteSocialById(socialId: Long) {
        sqlClient.deleteById(SysSocial::class, socialId)
    }

    /**
     * 实体转 VO
     * Note: BO/VO use `type`/`openid` while entity uses `source`/`openId`/`authId`
     */
    private fun entityToVo(social: SysSocial): SysSocialVo {
        return SysSocialVo(
            socialId = social.id,
            tenantId = social.tenantId,
            userId = social.userId,
            type = social.source,
            openid = social.openId ?: social.authId,
            accessToken = social.accessToken,
            expireIn = social.expireIn,
            refreshToken = social.refreshToken,
            createTime = social.createTime
        )
    }
}
