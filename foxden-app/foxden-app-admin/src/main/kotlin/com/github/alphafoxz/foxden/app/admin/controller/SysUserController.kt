package com.github.alphafoxz.foxden.app.admin.controller

import com.github.alphafoxz.foxden.domain.system.entity.SysUser
import com.github.alphafoxz.foxden.domain.system.entity.by
import com.github.alphafoxz.foxden.domain.system.repository.SysUserRepository
import org.babyfish.jimmer.sql.kt.fetcher.newFetcher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/sys/user")
class SysUserController(
    private val sysUserRepository: SysUserRepository
) {
    @GetMapping("/list")
    fun sysUserList(): ResponseEntity<List<SysUser>> {
        val list = sysUserRepository.findAll()
        return ResponseEntity.ok(list)
    }

    @GetMapping("/userName/{userName}")
    fun getUserName(@PathVariable userName: String): ResponseEntity<SysUser?> {
        val user = sysUserRepository.findByUserNameEqualsIgnoreCase(
            userName, ALL_FIELDS_FETCHER
        )
        return ResponseEntity.ok(user)
    }

    companion object {
        private val ALL_FIELDS_FETCHER = newFetcher(SysUser::class).by {
            allTableFields()
        }
    }
}

