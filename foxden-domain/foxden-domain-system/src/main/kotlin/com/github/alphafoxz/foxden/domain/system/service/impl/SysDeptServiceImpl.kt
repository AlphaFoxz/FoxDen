package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysDeptService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.domain.system.bo.SysDeptBo
import com.github.alphafoxz.foxden.domain.system.vo.SysDeptVo

/**
 * Dept 业务层处理
 */
@Service
class SysDeptServiceImpl(): SysDeptService {

    override fun selectDeptList(dept: SysDeptBo): List<SysDeptVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectDeptById(deptId: Long): SysDeptVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun selectNormalChildrenDeptById(deptId: Long): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun hasChildByDeptId(deptId: Long): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun checkDeptExistUser(deptId: Long): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun checkDeptNameUnique(dept: SysDeptBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun checkDeptDataScope(deptId: Long) {
        // TODO: 实现业务逻辑
    }

    override fun insertDept(bo: SysDeptBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateDept(bo: SysDeptBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteDeptById(deptId: Long): Int {
        // TODO: 实现业务逻辑
        return 0
    }
}
