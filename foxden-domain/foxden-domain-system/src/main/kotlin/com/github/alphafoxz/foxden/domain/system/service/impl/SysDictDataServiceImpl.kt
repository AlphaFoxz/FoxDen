package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysDictDataService
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.bo.SysDictDataBo
import com.github.alphafoxz.foxden.domain.system.vo.SysDictDataVo
import com.github.alphafoxz.foxden.common.core.constant.SystemConstants
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import org.babyfish.jimmer.sql.kt.ast.expression.*
import org.babyfish.jimmer.sql.kt.*
import org.springframework.stereotype.Service

/**
 * DictData 业务层处理
 */
@Service
class SysDictDataServiceImpl(
    private val sqlClient: KSqlClient
) : SysDictDataService {

    override fun selectDictDataList(dictData: SysDictDataBo): List<SysDictDataVo> {
        val dictDataList = sqlClient.createQuery(SysDictData::class) {
            dictData.dictCode?.let { where(table.id eq it) }
            dictData.dictType?.takeIf { it.isNotBlank() }?.let { where(table.dictType eq it) }
            dictData.dictLabel?.takeIf { it.isNotBlank() }?.let { where(table.dictLabel like "%${it}%") }
            orderBy(table.dictSort.asc())
            select(table)
        }.execute()

        return dictDataList.map { entityToVo(it) }
    }

    override fun selectDictDataByType(dictType: String): List<SysDictDataVo> {
        val dictDataList = sqlClient.createQuery(SysDictData::class) {
            where(table.dictType eq dictType)
            orderBy(table.dictSort.asc())
            select(table)
        }.execute()

        return dictDataList.map { entityToVo(it) }
    }

    override fun selectDictLabel(dictType: String, dictValue: String): String {
        val dictData = sqlClient.createQuery(SysDictData::class) {
            where(table.dictType eq dictType)
            where(table.dictValue eq dictValue)
            select(table.dictLabel)
        }.fetchOneOrNull()

        return dictData ?: ""
    }

    override fun selectDictDataById(dictCode: Long): SysDictDataVo? {
        val dictData = sqlClient.findById(SysDictData::class, dictCode) ?: return null
        return entityToVo(dictData)
    }

    override fun deleteDictDataByIds(dictCodes: Array<Long>) {
        sqlClient.deleteByIds(SysDictData::class, dictCodes.toList())
    }

    override fun insertDictData(bo: SysDictDataBo): Int {
        val newDictData = com.github.alphafoxz.foxden.domain.system.entity.SysDictDataDraft.`$`.produce {
            dictSort = bo.dictSort ?: 0
            dictLabel = bo.dictLabel ?: throw ServiceException("字典标签不能为空")
            dictValue = bo.dictValue
            dictType = bo.dictType ?: throw ServiceException("字典类型不能为空")
            cssClass = bo.cssClass
            listClass = bo.listClass
            defaultFlag = bo.isDefault ?: SystemConstants.NO
            remark = bo.remark
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.save(newDictData)
        return if (result.isModified) 1 else 0
    }

    override fun updateDictData(bo: SysDictDataBo): Int {
        val dictCodeVal = bo.dictCode ?: return 0

        val result = sqlClient.createUpdate(SysDictData::class) {
            where(table.id eq dictCodeVal)
            bo.dictSort?.let { set(table.dictSort, it) }
            bo.dictLabel?.let { set(table.dictLabel, it) }
            bo.dictValue?.let { set(table.dictValue, it) }
            bo.dictType?.let { set(table.dictType, it) }
            bo.cssClass?.let { set(table.cssClass, it) }
            bo.listClass?.let { set(table.listClass, it) }
            bo.isDefault?.let { set(table.defaultFlag, it) }
            bo.remark?.let { set(table.remark, it) }
            set(table.updateTime, java.time.LocalDateTime.now())
        }.execute()
        return result
    }

    override fun checkDictDataUnique(dictData: SysDictDataBo): Boolean {
        val existing = sqlClient.createQuery(SysDictData::class) {
            where(table.dictType eq dictData.dictType)
            where(table.dictValue eq dictData.dictValue)
            dictData.dictCode?.let { where(table.id ne it) }
            select(table)
        }.fetchOneOrNull()

        return existing == null || existing.id == dictData.dictCode
    }

    /**
     * 实体转 VO
     */
    private fun entityToVo(dictData: SysDictData): SysDictDataVo {
        return SysDictDataVo(
            dictCode = dictData.id,
            dictSort = dictData.dictSort,
            dictLabel = dictData.dictLabel,
            dictValue = dictData.dictValue,
            dictType = dictData.dictType,
            cssClass = dictData.cssClass,
            listClass = dictData.listClass,
            isDefault = dictData.defaultFlag,
            remark = dictData.remark,
            createTime = dictData.createTime
        )
    }
}
