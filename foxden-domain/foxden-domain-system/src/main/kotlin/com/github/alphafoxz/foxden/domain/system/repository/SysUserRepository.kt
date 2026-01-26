package com.github.alphafoxz.foxden.domain.system.repository

import com.github.alphafoxz.foxden.domain.system.entity.SysUser
import org.babyfish.jimmer.spring.repository.KRepository
import org.babyfish.jimmer.sql.fetcher.Fetcher

interface SysUserRepository : KRepository<SysUser, Long> {
    fun findByUserNameEquals(username: String, fetcher: Fetcher<SysUser>): SysUser?
}
