package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.common.core.constant.CacheNames
import com.github.alphafoxz.foxden.common.core.domain.dto.DictDataDTO
import com.github.alphafoxz.foxden.common.core.domain.dto.DictTypeDTO
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.service.DictService
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.StreamUtils
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.common.redis.utils.CacheUtils
import com.github.alphafoxz.foxden.domain.system.bo.SysDictTypeBo
import com.github.alphafoxz.foxden.domain.system.entity.*
import com.github.alphafoxz.foxden.domain.system.service.SysDictTypeService
import com.github.alphafoxz.foxden.domain.system.service.extensions.saveWithAutoId
import com.github.alphafoxz.foxden.domain.system.vo.SysDictDataVo
import com.github.alphafoxz.foxden.domain.system.vo.SysDictTypeVo
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.babyfish.jimmer.sql.kt.ast.expression.asc
import org.babyfish.jimmer.sql.kt.ast.expression.eq
import org.babyfish.jimmer.sql.kt.ast.expression.like
import org.babyfish.jimmer.sql.kt.ast.expression.ne
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * DictType 业务层处理
 */
@Service
class SysDictTypeServiceImpl(
    private val sqlClient: KSqlClient
) : SysDictTypeService, DictService {

    override fun selectPageDictTypeList(dictType: SysDictTypeBo, pageQuery: PageQuery): TableDataInfo<SysDictTypeVo> {
        val dictTypes = sqlClient.createQuery(SysDictType::class) {
            dictType.dictName?.takeIf { it.isNotBlank() }?.let { where(table.dictName like "%${it}%") }
            dictType.dictType?.takeIf { it.isNotBlank() }?.let { where(table.dictType like "%${it}%") }
            orderBy(table.id.asc())
            select(table)
        }.execute()

        // 使用假分页
        return TableDataInfo.build(
            dictTypes.map { entityToVo(it) },
            pageQuery.getPageNumOrDefault(),
            pageQuery.getPageSizeOrDefault()
        )
    }

    override fun selectDictTypeList(dictType: SysDictTypeBo): List<SysDictTypeVo> {
        val dictTypes = sqlClient.createQuery(SysDictType::class) {
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

    @Cacheable(cacheNames = [CacheNames.SYS_DICT], key = "#dictType")
    override fun selectDictDataByType(dictType: String): List<SysDictDataVo>? {
        val dictDataList = sqlClient.createQuery(SysDictData::class) {
            where(table.dictType eq dictType)
            orderBy(table.dictSort.asc())
            select(table)
        }.execute()

        val result = dictDataList.map { dictDataEntityToVo(it) }
        return if (result.isNotEmpty()) result else null
    }

    override fun selectDictTypeById(dictId: Long): SysDictTypeVo? {
        val dictType = sqlClient.findById(SysDictType::class, dictId) ?: return null
        return entityToVo(dictType)
    }

    @Cacheable(cacheNames = [CacheNames.SYS_DICT_TYPE], key = "#dictType")
    override fun selectDictTypeByType(dictType: String): SysDictTypeVo? {
        val dictTypeEntity = sqlClient.createQuery(SysDictType::class) {
            where(table.dictType eq dictType)
            select(table)
        }.fetchOneOrNull()

        return dictTypeEntity?.let { entityToVo(it) }
    }

    override fun deleteDictTypeByIds(dictIds: Array<Long>) {
        // 查询要删除的字典类型
        val list = sqlClient.findByIds(SysDictType::class, dictIds.toList())
        // 检查是否有关联的字典数据
        list.forEach { dictType ->
            val assigned = sqlClient.createQuery(SysDictData::class) {
                where(table.dictType eq dictType.dictType)
                select(table.id)
            }.execute().isNotEmpty()

            if (assigned) {
                throw ServiceException("${dictType.dictName}已分配,不能删除")
            }
        }

        // 执行删除
        sqlClient.deleteByIds(SysDictType::class, dictIds.toList())

        // 清除缓存
        list.forEach {
            CacheUtils.evict(CacheNames.SYS_DICT, it.dictType)
            CacheUtils.evict(CacheNames.SYS_DICT_TYPE, it.dictType)
        }
    }

    override fun resetDictCache() {
        CacheUtils.clear(CacheNames.SYS_DICT)
        CacheUtils.clear(CacheNames.SYS_DICT_TYPE)
    }

    @CachePut(cacheNames = [CacheNames.SYS_DICT], key = "#bo.dictType")
    @Transactional(rollbackFor = [Exception::class])
    override fun insertDictType(bo: SysDictTypeBo): List<SysDictDataVo> {
        val newDictType = com.github.alphafoxz.foxden.domain.system.entity.SysDictTypeDraft.`$`.produce {
            dictName = bo.dictName ?: throw ServiceException("字典名称不能为空")
            dictType = bo.dictType ?: throw ServiceException("字典类型不能为空")
            remark = bo.remark
            createTime = java.time.LocalDateTime.now()
        }

        val result = sqlClient.saveWithAutoId(newDictType)
        if (result.isModified) {
            // 新增 type 下无 data 数据 返回空防止缓存穿透
            return emptyList()
        }
        throw ServiceException("操作失败")
    }

    @CachePut(cacheNames = [CacheNames.SYS_DICT], key = "#bo.dictType")
    @Transactional(rollbackFor = [Exception::class])
    override fun updateDictType(bo: SysDictTypeBo): List<SysDictDataVo> {
        val dictIdVal = bo.dictId ?: throw ServiceException("字典ID不能为空")
        val oldDict = sqlClient.findById(SysDictType::class, dictIdVal)
            ?: throw ServiceException("字典类型不存在")

        // 如果字典类型发生变化，需要同步更新字典数据
        if (bo.dictType != null && bo.dictType != oldDict.dictType) {
            // 使用Jimmer的update语句
            sqlClient.createUpdate(SysDictData::class) {
                where(table.dictType eq oldDict.dictType)
                set(table.dictType, bo.dictType!!)
            }.execute()
            // 清除旧缓存
            CacheUtils.evict(CacheNames.SYS_DICT, oldDict.dictType)
            CacheUtils.evict(CacheNames.SYS_DICT_TYPE, oldDict.dictType)
        }

        // Update SysDictType using createUpdate
        sqlClient.createUpdate(SysDictType::class) {
            where(table.id eq dictIdVal)
            bo.dictName?.let { set(table.dictName, it) }
            bo.dictType?.let { set(table.dictType, it) }
            bo.remark?.let { set(table.remark, it) }
            set(table.updateTime, java.time.LocalDateTime.now())
        }.execute()

        // 返回更新后的字典数据列表
        return selectDictDataByType(bo.dictType ?: oldDict.dictType) ?: emptyList()
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
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    override fun getDictLabel(dictType: String, dictValue: String, separator: String): String {
        val datas = SpringUtils.getAopProxy(this).selectDictDataByType(dictType) ?: return ""
        val map = StreamUtils.toMap(datas, SysDictDataVo::dictValue, SysDictDataVo::dictLabel)
        return if (StringUtils.containsAny(dictValue, separator)) {
            dictValue.split(separator)
                .map { v -> map[v] ?: "" }
                .joinToString(separator)
        } else {
            map[dictValue] ?: ""
        }
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @param separator 分隔符
     * @return 字典值
     */
    override fun getDictValue(dictType: String, dictLabel: String, separator: String): String {
        val datas = SpringUtils.getAopProxy(this).selectDictDataByType(dictType) ?: return ""
        val map = StreamUtils.toMap(datas, SysDictDataVo::dictLabel, SysDictDataVo::dictValue)
        return if (StringUtils.containsAny(dictLabel, separator)) {
            dictLabel.split(separator)
                .map { l -> map[l] ?: "" }
                .joinToString(separator)
        } else {
            map[dictLabel] ?: ""
        }
    }

    /**
     * 获取字典下所有的字典值与标签
     *
     * @param dictType 字典类型
     * @return dictValue为key，dictLabel为值组成的Map
     */
    override fun getAllDictByDictType(dictType: String): Map<String, String> {
        val list = SpringUtils.getAopProxy(this).selectDictDataByType(dictType) ?: return emptyMap()
        // 保证顺序
        val map = linkedMapOf<String, String>()
        list.forEach { vo ->
            vo.dictValue?.let { map[it] = vo.dictLabel ?: "" }
        }
        return map
    }

    /**
     * 根据字典类型查询详细信息
     *
     * @param dictType 字典类型
     * @return 字典类型详细信息
     */
    override fun getDictType(dictType: String): DictTypeDTO? {
        val vo = SpringUtils.getAopProxy(this).selectDictTypeByType(dictType)
        return vo?.let {
            DictTypeDTO(
                dictId = it.dictId,
                dictName = it.dictName,
                dictType = it.dictType,
                remark = it.remark
            )
        }
    }

    /**
     * 根据字典类型查询字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    override fun getDictData(dictType: String): List<DictDataDTO> {
        val list = SpringUtils.getAopProxy(this).selectDictDataByType(dictType) ?: return emptyList()
        return list.map { vo ->
            DictDataDTO(
                dictLabel = vo.dictLabel,
                dictValue = vo.dictValue,
                isDefault = vo.isDefault,
                remark = vo.remark
            )
        }
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

    /**
     * 字典数据实体转 VO
     */
    private fun dictDataEntityToVo(dictData: SysDictData): SysDictDataVo {
        return SysDictDataVo(
            dictCode = dictData.id,
            dictSort = dictData.dictSort,
            dictLabel = dictData.dictLabel,
            dictValue = dictData.dictValue,
            dictType = dictData.dictType,
            cssClass = dictData.cssClass,
            listClass = dictData.listClass,
            remark = dictData.remark,
            createTime = dictData.createTime
        )
    }

    /**
     * 根据字典类型和字典值获取字典标签（用于翻译功能）
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    override fun selectDictLabel(dictType: String, dictValue: String?): String? {
        if (dictValue.isNullOrBlank()) return null
        val dictDataList = selectDictDataByType(dictType) ?: return null
        return dictDataList.find { it.dictValue == dictValue }?.dictLabel
    }
}
