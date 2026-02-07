package com.github.alphafoxz.foxden.domain.system.service.impl

import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import org.springframework.stereotype.Service
import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.system.bo.SysUserBo
import com.github.alphafoxz.foxden.domain.system.vo.SysUserExportVo
import com.github.alphafoxz.foxden.domain.system.vo.SysUserVo

/**
 * User 业务层处理
 */
import com.github.alphafoxz.foxden.common.core.domain.dto.UserDTO
@Service
class SysUserServiceImpl(): SysUserService {

    override fun selectPageUserList(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        // TODO: 实现业务逻辑
        return com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo.build(emptyList())
    }

    override fun selectUserExportList(user: SysUserBo): List<SysUserExportVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectAllocatedList(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        // TODO: 实现业务逻辑
        return com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo.build(emptyList())
    }

    override fun selectUnallocatedList(user: SysUserBo, pageQuery: PageQuery): TableDataInfo<SysUserVo> {
        // TODO: 实现业务逻辑
        return com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo.build(emptyList())
    }

    override fun selectUserByUserName(userName: String): SysUserVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun selectUserByPhonenumber(phonenumber: String): SysUserVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun selectUserById(userId: Long): SysUserVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun selectUserByEmail(email: String): SysUserVo? {
        // TODO: 实现业务逻辑
        return null
    }

    override fun selectUserByIds(userIds: List<Long>, deptId: Long?): List<SysUserVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }

    override fun selectUserRoleGroup(userId: Long): String {
    return ""
        // TODO: 实现业务逻辑
    }

    override fun selectUserPostGroup(userId: Long): String {
    return ""
        // TODO: 实现业务逻辑
    }

    override fun checkUserNameUnique(user: SysUserBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun checkPhoneUnique(user: SysUserBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun checkEmailUnique(user: SysUserBo): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun checkUserAllowed(userId: Long) {
        // TODO: 实现业务逻辑
    }

    override fun checkUserDataScope(userId: Long) {
        // TODO: 实现业务逻辑
    }

    override fun insertUser(user: SysUserBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun registerUser(user: SysUserBo, tenantId: String?): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun updateUser(user: SysUserBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun insertUserAuth(userId: Long, roleIds: Array<Long>) {
        // TODO: 实现业务逻辑
    }

    override fun updateUserStatus(userId: Long, status: String): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateUserProfile(user: SysUserBo): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun updateUserAvatar(userId: Long, avatar: Long): Boolean {
        // TODO: 实现业务逻辑
        return true
    }

    override fun resetUserPwd(userId: Long, password: String): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteUserById(userId: Long): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun deleteUserByIds(userIds: Array<Long>): Int {
        // TODO: 实现业务逻辑
        return 0
    }

    override fun selectUserListByDept(deptId: Long): List<SysUserVo> {
        // TODO: 实现业务逻辑
        return emptyList()
    }
}
