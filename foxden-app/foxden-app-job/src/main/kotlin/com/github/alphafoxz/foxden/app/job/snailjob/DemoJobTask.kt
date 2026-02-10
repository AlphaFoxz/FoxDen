package com.github.alphafoxz.foxden.app.job.snailjob

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor
import com.aizuda.snailjob.client.job.core.dto.JobArgs
import com.aizuda.snailjob.common.log.SnailJobLog
import com.aizuda.snailjob.model.dto.ExecuteResult
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * 演示定时任务
 *
 * @author opensnail
 */
@Component
@JobExecutor(name = "demoJobTask")
class DemoJobTask {

    fun jobExecute(jobArgs: JobArgs): ExecuteResult {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val message = "DemoJobTask executed at: ${now.format(formatter)}"

        SnailJobLog.REMOTE.info(message)
        return ExecuteResult.success(message)
    }
}
