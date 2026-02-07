package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysDictTypeService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.domain.system.bo.SysDictTypeBo
import com.github.alphafoxz.foxden.domain.system.vo.SysDictTypeVo

/**
 * DictType 业务层处理
 */
@Service
class SysDictTypeServiceImpl(): SysDictTypeService {

    override fun selectDictTypeList(dictType: SysDictTypeBo): List<SysDictTypeVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectDictTypeAll(): List<SysDictTypeVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectDictDataByType(dictType: String): Map<String, List<SysDictTypeVo>> {
        // TODO: 实现业务逻辑
        return emptyMap()
    }

    override fun selectDictTypeById(dictId: Long): SysDictTypeVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun selectDictTypeByType(dictType: String): SysDictTypeVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun deleteDictTypeByIds(dictIds: Array<Long>) {
        // TODO: 实现业务逻辑
    }

    override fun clearDictCache() {
        // TODO: 实现业务逻辑
    }

    override fun resetDictCache() {
        // TODO: 实现业务逻辑
    }

    override fun insertDictType(bo: SysDictTypeBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateDictType(bo: SysDictTypeBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun checkDictTypeUnique(dictType: SysDictTypeBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }
}
