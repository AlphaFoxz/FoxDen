package com.github.alphafoxz.foxden.domain.system.service

import com.github.alphafoxz.foxden.domain.system.bo.SysDictTypeBo
import com.github.alphafoxz.foxden.domain.system.vo.SysDictTypeVo

/**
 * 字典类型 业务层
 *
 * @author Lion Li
 */
interface SysDictTypeService {

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合
     */
    fun selectDictTypeList(dictType: SysDictTypeBo): List<SysDictTypeVo>

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合
     */
    fun selectDictTypeAll(): List<SysDictTypeVo>

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合
     */
    fun selectDictDataByType(dictType: String): Map<String, List<SysDictTypeVo>>

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    fun selectDictTypeById(dictId: Long): SysDictTypeVo?

    /**
     * 根据字典类型查询信息
     *
     * @param dictType 字典类型
     * @return 字典类型
     */
    fun selectDictTypeByType(dictType: String): SysDictTypeVo?

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     */
    fun deleteDictTypeByIds(dictIds: Array<Long>)

    /**
     * 清空缓存数据
     */
    fun clearDictCache()

    /**
     * 重置字典缓存数据
     */
    fun resetDictCache()

    /**
     * 新增保存字典类型信息
     *
     * @param bo 字典类型信息
     * @return 结果
     */
    fun insertDictType(bo: SysDictTypeBo): Int

    /**
     * 修改保存字典类型信息
     *
     * @param bo 字典类型信息
     * @return 结果
     */
    fun updateDictType(bo: SysDictTypeBo): Int

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    fun checkDictTypeUnique(dictType: SysDictTypeBo): Boolean
}
