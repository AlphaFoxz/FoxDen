package com.github.alphafoxz.foxden.common.core.service

import com.github.alphafoxz.foxden.common.core.domain.dto.DictDataDTO
import com.github.alphafoxz.foxden.common.core.domain.dto.DictTypeDTO

/**
 * 通用 字典服务
 *
 * @author Lion Li
 */
interface DictService {
    companion object {
        /**
         * 分隔符
         */
        const val SEPARATOR = ","
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    fun getDictLabel(dictType: String, dictValue: String, separator: String = SEPARATOR): String

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @param separator 分隔符
     * @return 字典值
     */
    fun getDictValue(dictType: String, dictLabel: String, separator: String = SEPARATOR): String

    /**
     * 获取字典下所有的字典值与标签
     *
     * @param dictType 字典类型
     * @return dictValue为key，dictLabel为值组成的Map
     */
    fun getAllDictByDictType(dictType: String): Map<String, String>

    /**
     * 根据字典类型查询详细信息
     *
     * @param dictType 字典类型
     * @return 字典类型详细信息
     */
    fun getDictType(dictType: String): DictTypeDTO?

    /**
     * 根据字典类型查询字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    fun getDictData(dictType: String): List<DictDataDTO>

    /**
     * 根据字典类型和字典值获取字典标签（用于翻译功能）
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @return 字典标签
     */
    fun selectDictLabel(dictType: String, dictValue: String?): String?
}
