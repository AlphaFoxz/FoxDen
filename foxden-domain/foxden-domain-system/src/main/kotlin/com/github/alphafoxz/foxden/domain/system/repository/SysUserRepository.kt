package com.github.alphafoxz.foxden.domain.system.repository

import com.github.alphafoxz.foxden.domain.system.entity.SysUser
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.sql.fetcher.Fetcher

interface SysUserRepository : KRepository<SysUser, Long> {
    // Jimmer will generate findByUserNameEqualsIgnoreCase automatically
    // based on the property name in SysUser entity
    // Add @Suppress to avoid IDE warnings
    @Suppress("SpringDataRepositoryMethodReturnType", "SpringDataMethodInconsistency")
    fun findByUserNameEqualsIgnoreCase(userName: String, fetcher: Fetcher<SysUser>): SysUser?
}
