package com.github.alphafoxz.foxden.app.admin

import org.babyfish.jimmer.client.EnableImplicitApi
import org.babyfish.jimmer.spring.repository.EnableJimmerRepositories
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableImplicitApi
@EnableJimmerRepositories(basePackages = ["com.github.alphafoxz.foxden.domain"])
class FoxdenAdminApplication

fun main(args: Array<String>) {
    runApplication<FoxdenAdminApplication>(*args)
}
