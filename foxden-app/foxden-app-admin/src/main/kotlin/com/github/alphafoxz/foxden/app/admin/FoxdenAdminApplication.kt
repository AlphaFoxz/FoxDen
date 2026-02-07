package com.github.alphafoxz.foxden.app.admin

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * FoxDen Admin Application
 * 启动程序
 *
 * @author FoxDen Team
 */
@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = [
    "com.github.alphafoxz.foxden.app.admin",
    "com.github.alphafoxz.foxden.domain",
    "com.github.alphafoxz.foxden.common"
])
class FoxdenAdminApplication

fun main(args: Array<String>) {
    val application = SpringApplication(FoxdenAdminApplication::class.java)
    application.setApplicationStartup(BufferingApplicationStartup(2048))
    application.run(*args)
    println("(♥◠‿◠)ﾉﾞ  FoxDen Admin启动成功   ლ(´ڡ`ლ)ﾞ")
}
