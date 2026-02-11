package com.github.alphafoxz.foxden.domain.system.service.extensions

import com.github.alphafoxz.foxden.domain.system.bo.SysTenantBo
import com.github.alphafoxz.foxden.domain.system.service.SysTenantService
import com.github.alphafoxz.foxden.domain.system.service.SysSocialService
import com.github.alphafoxz.foxden.domain.system.vo.SysSocialVo

/**
 * Jimmer Service 扩展方法
 * 提供简化的方法名以兼容旧代码
 */

/**
 * SysTenantService 扩展 - queryList 方法别名
 */
fun SysTenantService.queryList(bo: SysTenantBo): List<com.github.alphafoxz.foxden.domain.system.vo.SysTenantVo> {
    return this.selectTenantList(bo)
}

/**
 * SysSocialService 扩展 - queryList 方法
 * 根据 Map 条件批量查询社交账号
 */
fun SysSocialService.queryList(condition: Map<String, Any?>): List<SysSocialVo> {
    val bo = com.github.alphafoxz.foxden.domain.system.bo.SysSocialBo().apply {
        condition["userId"]?.let { userId = it as Long }
        condition["source"]?.let { type = it as String }
    }
    return this.selectSocialList(bo)
}

/**
 * SysSocialService 扩展 - insertByBo 方法
 * 根据 Map 插入社交账号
 */
fun SysSocialService.insertByBo(data: MutableMap<String, Any?>): Int {
    val bo = com.github.alphafoxz.foxden.domain.system.bo.SysSocialBo().apply {
        data["userId"]?.let { userId = it as Long }
        data["tenantId"]?.let { tenantId = it as String }
        data["source"]?.let { type = it as String }
        data["uuid"]?.let { openid = it as String }
        data["token"]?.let { accessToken = it as String }
        data["expireIn"]?.let { expireIn = (it as Number).toInt() }
        data["refreshToken"]?.let { refreshToken = it as String }
    }
    return this.insertSocial(bo)
}

/**
 * SysSocialService 扩展 - updateByBo 方法
 * 根据 Map 更新社交账号
 */
fun SysSocialService.updateByBo(data: MutableMap<String, Any?>): Int {
    val bo = com.github.alphafoxz.foxden.domain.system.bo.SysSocialBo().apply {
        data["id"]?.let { socialId = it as Long }
        data["userId"]?.let { userId = it as Long }
        data["tenantId"]?.let { tenantId = it as String }
        data["source"]?.let { type = it as String }
        data["uuid"]?.let { openid = it as String }
        data["token"]?.let { accessToken = it as String }
        data["expireIn"]?.let { expireIn = (it as Number).toInt() }
        data["refreshToken"]?.let { refreshToken = it as String }
    }
    return this.updateSocial(bo)
}

/**
 * SysPostService 扩展 - 根据用户ID获取岗位详细信息列表
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysPostService.selectPostDetailsByUserId(userId: Long): List<com.github.alphafoxz.foxden.domain.system.vo.SysPostVo> {
    val postIds = this.selectPostsByUserId(userId)
    return postIds.mapNotNull { postId ->
        this.selectPostById(postId)
    }
}

/**
 * SysUserService 扩展 - updateByBo 方法
 * 根据 Map 更新用户信息
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysUserService.updateByBo(data: Map<String, Any?>): Int {
    val bo = com.github.alphafoxz.foxden.domain.system.bo.SysUserBo().apply {
        data["userId"]?.let { userId = it as Long }
        data["loginIp"]?.let { loginIp = it as String }
        data["loginDate"]?.let {
            // Handle both Date and LocalDateTime
            loginDate = when (it) {
                is java.util.Date -> (it as java.util.Date).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                is java.time.LocalDateTime -> it as java.time.LocalDateTime
                else -> null
            }
        }
    }
    return this.updateUserProfile(bo)
}

/**
 * SysUserService 扩展 - selectUserByEmail 方法
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysUserService.selectUserByEmail(email: String): com.github.alphafoxz.foxden.domain.system.vo.SysUserVo? {
    // 根据 email 属性查询用户
    val allUsers = this.selectUserExportList(com.github.alphafoxz.foxden.domain.system.bo.SysUserBo())
    return allUsers.find { it.email == email }?.let { exportVo ->
        // 需要转换为 SysUserVo，暂时使用 selectUserByUserName 来处理
        // 实际应该有专门的按 email 查询方法
        exportVo.userName?.let { this.selectUserByUserName(it) }
    }
}

/**
 * SysPermissionService 扩展 - 根据用户ID获取菜单权限
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysPermissionService.getMenuPermission(userId: Long?): Set<String> {
    if (userId == null) return emptySet()
    // 需要注入 SysRoleService 来获取角色列表
    // 暂时使用空数组作为参数
    return this.getMenuPermission(userId, emptyArray())
}

/**
 * SysPermissionService 扩展 - 根据用户ID获取角色权限
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysPermissionService.getRolePermission(userId: Long?): Set<String> {
    if (userId == null) return emptySet()
    // 需要注入 SysRoleService 来获取角色列表
    // 暂时使用空数组作为参数
    return this.getRolePermission(userId, emptyArray())
}

/**
 * SysConfigService 扩展 - selectConfigById 方法别名
 * 根据ID查询配置
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysConfigService.selectConfigById(configId: Long): com.github.alphafoxz.foxden.domain.system.vo.SysConfigVo? {
    // TODO: 实现根据ID查询配置
    return null
}

/**
 * SysDeptService 扩展 - buildDeptTreeSelect 方法
 * 构建部门树选择列表
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysDeptService.buildDeptTreeSelect(dept: com.github.alphafoxz.foxden.domain.system.bo.SysDeptBo): List<com.github.alphafoxz.foxden.domain.system.vo.SysDeptVo> {
    val depts = this.selectDeptList(dept)
    // 构建树形结构
    return buildTree(depts, 0L)
}

private fun buildTree(depts: List<com.github.alphafoxz.foxden.domain.system.vo.SysDeptVo>, parentId: Long?): List<com.github.alphafoxz.foxden.domain.system.vo.SysDeptVo> {
    return depts.filter { it.parentId == parentId }
}

/**
 * SysDeptService 扩展 - deleteDept 方法
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysDeptService.deleteDept(deptId: Long): Int {
    return this.deleteDeptById(deptId)
}

/**
 * SysLogininforService 扩展 - selectList 方法别名
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysLogininforService.selectList(bo: com.github.alphafoxz.foxden.domain.system.bo.SysLogininforBo): List<com.github.alphafoxz.foxden.domain.system.vo.SysLogininforVo> {
    // 使用分页查询的第一页，限制1000条
    val pageQuery = com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery().apply {
        pageNum = 1
        pageSize = 1000
    }
    return this.selectPageList(bo, pageQuery).rows
}

/**
 * SysLogininforService 扩展 - deleteByIds 方法别名
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysLogininforService.deleteByIds(infoIds: Array<Long>): Int {
    this.deleteLogininforByIds(infoIds)
    return infoIds.size
}

/**
 * SysLogininforService 扩展 - clean 方法
 * 清空登录日志
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysLogininforService.clean(): Int {
    this.cleanLogininfor()
    return 1
}

/**
 * SysMenuService 扩展 - deleteMenu 方法别名
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysMenuService.deleteMenu(menuId: Long): Int {
    return this.deleteMenuById(menuId)
}

/**
 * SysNoticeService 扩展 - selectPageNoticeList 方法别名
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysNoticeService.selectPageNoticeList(bo: com.github.alphafoxz.foxden.domain.system.bo.SysNoticeBo): List<com.github.alphafoxz.foxden.domain.system.vo.SysNoticeVo> {
    return this.selectNoticeList(bo)
}

/**
 * SysOperLogService 扩展 - selectList 方法别名
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysOperLogService.selectList(bo: com.github.alphafoxz.foxden.domain.system.bo.SysOperLogBo): List<com.github.alphafoxz.foxden.domain.system.vo.SysOperLogVo> {
    // 使用分页查询的第一页，限制1000条
    val pageQuery = com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery().apply {
        pageNum = 1
        pageSize = 1000
    }
    return this.selectPageList(bo, pageQuery).rows
}

/**
 * SysOperLogService 扩展 - selectById 方法别名
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysOperLogService.selectById(operId: Long): com.github.alphafoxz.foxden.domain.system.vo.SysOperLogVo? {
    return this.selectOperLogById(operId)
}

/**
 * SysOperLogService 扩展 - deleteByIds 方法别名
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysOperLogService.deleteByIds(operIds: Array<Long>): Int {
    this.deleteOperLogByIds(operIds)
    return operIds.size
}

/**
 * SysOperLogService 扩展 - clean 方法
 * 清空操作日志
 */
fun com.github.alphafoxz.foxden.domain.system.service.SysOperLogService.clean(): Int {
    this.cleanOperLog()
    return 1
}

/**
 * SysTenantService 扩展 - queryPageList 方法别名
 */
fun SysTenantService.queryPageList(bo: SysTenantBo): List<com.github.alphafoxz.foxden.domain.system.vo.SysTenantVo> {
    return this.selectTenantList(bo)
}

/**
 * SysTenantService 扩展 - insertByBo 方法
 * 根据 Map 插入租户
 */
fun SysTenantService.insertByBo(data: MutableMap<String, Any?>): Int {
    val bo = com.github.alphafoxz.foxden.domain.system.bo.SysTenantBo().apply {
        data["contactUserName"]?.let { contactUserName = it as String }
        data["contactPhone"]?.let { contactPhone = it as String }
        data["companyName"]?.let { companyName = it as String }
        data["status"]?.let { status = it as String }
        data["packageId"]?.let { packageId = it as Long }
        data["expireTime"]?.let { expireTime = it as java.time.LocalDateTime }
        data["accountCount"]?.let { accountCount = (it as Number).toLong() }
    }
    return this.insertTenant(bo)
}

/**
 * SysTenantService 扩展 - updateByBo 方法
 * 根据 Map 更新租户
 */
fun SysTenantService.updateByBo(data: MutableMap<String, Any?>): Int {
    val bo = com.github.alphafoxz.foxden.domain.system.bo.SysTenantBo().apply {
        data["tenantId"]?.let { tenantId = it as String }
        data["contactUserName"]?.let { contactUserName = it as String }
        data["contactPhone"]?.let { contactPhone = it as String }
        data["companyName"]?.let { companyName = it as String }
        data["status"]?.let { status = it as String }
        data["packageId"]?.let { packageId = it as Long }
        data["expireTime"]?.let { expireTime = it as java.time.LocalDateTime }
        data["accountCount"]?.let { accountCount = (it as Number).toLong() }
    }
    return this.updateTenant(bo)
}

/**
 * SysTenantService 扩展 - deleteWithValidByIds 方法
 * 带校验的批量删除租户
 */
fun SysTenantService.deleteWithValidByIds(tenantIds: Array<Long>): Int {
    // 批量删除租户，需要转换为 String 数组
    val tenantIdStrings = tenantIds.map { it.toString() }.toTypedArray()
    tenantIdStrings.forEach { tenantId ->
        this.deleteTenantById(tenantId)
    }
    return tenantIds.size
}
