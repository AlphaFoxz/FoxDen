package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysDictTypeService
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.bo.SysDictTypeBo
import com.github.alphafoxz.foxden.domain.system.vo.SysDictTypeVo
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.*
import org.springframework.stereotype.Service

/**
 * DictType 业务层处理
 */
@Service
class SysDictTypeServiceImpl(
    private val sqlClient: KSqlClient
) : SysDictTypeService {

    override fun selectDictTypeList(dictType: SysDictTypeBo): List<SysDictTypeVo> {
        val dictTypes = sqlClient.createQuery(SysDictType::class) {
            dictType.dictId?.let { where(table.id eq it) }
            dictType.dictName?.takeIf { it.isNotBlank() }?.let { where(table.dictName like "%${it}%") }
            dictType.dictType?.takeIf { it.isNotBlank() }?.let { where(table.dictType like "%${it}%") }
            orderBy(table.id.asc())
            select(table)
        }.execute()

        return dictTypes.map { entityToVo(it) }
    }

    override fun selectDictTypeAll(): List<SysDictTypeVo> {
        val dictTypes = sqlClient.createQuery(SysDictType::class) {
            orderBy(table.id.asc())
            select(table)
        }.execute()

        return dictTypes.map { entityToVo(it) }
    }

    override fun selectDictDataByType(dictType: String): Map<String, List<SysDictTypeVo>> {
        // TODO: This method's return type seems incorrect - it returns Map<String, List<SysDictTypeVo>>
        // but should probably return Map<String, List<SysDictDataVo>> or similar
        // For now, return empty map as placeholder
        return emptyMap()
    }

    override fun selectDictTypeById(dictId: Long): SysDictTypeVo? {
        val dictType = sqlClient.findById(SysDictType::class, dictId) ?: return null
        return entityToVo(dictType)
    }

    override fun selectDictTypeByType(dictType: String): SysDictTypeVo? {
        val dictTypeEntity = sqlClient.createQuery(SysDictType::class) {
            where(table.dictType eq dictType)
            select(table)
        }.fetchOneOrNull()

        return dictTypeEntity?.let { entityToVo(it) }
    }

    override fun deleteDictTypeByIds(dictIds: Array<Long>) {
        sqlClient.deleteByIds(SysDictType::class, dictIds.toList())
    }

    override fun clearDictCache() {
        // TODO: Implement cache clearing logic when cache is integrated
    }

    override fun resetDictCache() {
        // TODO: Implement cache reset logic when cache is integrated
    }

    override fun insertDictType(bo: SysDictTypeBo): Int {
        val newDictType = com.github.alphafoxz.foxden.domain.system.entity.SysDictTypeDraft.`$`.produce {
            dictName = bo.dictName ?: throw ServiceException("字典名称不能为空")
            dictType = bo.dictType ?: throw ServiceException("字典类型不能为空")
            remark = bo.remark
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(newDictType)
        return if (result.isModified) 1 else 0
    }

    override fun updateDictType(bo: SysDictTypeBo): Int {
        val dictIdVal = bo.dictId ?: return 0
        val existing = sqlClient.findById(SysDictType::class, dictIdVal)
            ?: throw ServiceException("字典类型不存在")

        val updated = com.github.alphafoxz.foxden.domain.system.entity.SysDictTypeDraft.`$`.produce(existing) {
            bo.dictName?.let { dictName = it }
            bo.dictType?.let { dictType = it }
            bo.remark?.let { remark = it }
            updateTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(updated)
        return if (result.isModified) 1 else 0
    }

    override fun checkDictTypeUnique(dictType: SysDictTypeBo): Boolean {
        val existing = sqlClient.createQuery(SysDictType::class) {
            where(table.dictType eq dictType.dictType)
            dictType.dictId?.let { where(table.id ne it) }
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == dictType.dictId
    }

    /**
     * 实体转 VO
     */
    private fun entityToVo(dictType: SysDictType): SysDictTypeVo {
        return SysDictTypeVo(
            dictId = dictType.id,
            dictName = dictType.dictName,
            dictType = dictType.dictType,
            remark = dictType.remark,
            createTime = dictType.createTime
        )
    }
}
