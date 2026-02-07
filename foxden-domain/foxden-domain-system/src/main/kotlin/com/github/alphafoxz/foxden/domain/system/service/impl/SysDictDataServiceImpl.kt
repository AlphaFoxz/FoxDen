package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysDictDataService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.domain.system.bo.SysDictDataBo
import com.github.alphafoxz.foxden.domain.system.vo.SysDictDataVo

/**
 * DictData 业务层处理
 */
@Service
class SysDictDataServiceImpl(): SysDictDataService {

    override fun selectDictDataList(dictData: SysDictDataBo): List<SysDictDataVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectDictDataByType(dictType: String): List<SysDictDataVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectDictLabel(dictType: String, dictValue: String): String {
    return ""
        // TODO: 实现业务逻辑
    }

    override fun selectDictDataById(dictCode: Long): SysDictDataVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun deleteDictDataByIds(dictCodes: Array<Long>) {
        // TODO: 实现业务逻辑
    }

    override fun insertDictData(bo: SysDictDataBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateDictData(bo: SysDictDataBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun checkDictDataUnique(dictData: SysDictDataBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }
}
