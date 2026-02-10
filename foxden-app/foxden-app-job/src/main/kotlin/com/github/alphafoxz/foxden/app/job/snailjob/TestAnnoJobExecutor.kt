package com.github.alphafoxz.foxden.app.job.snailjob

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor
import com.aizuda.snailjob.client.job.core.dto.JobArgs
import com.aizuda.snailjob.common.log.SnailJobLog
import com.aizuda.snailjob.model.dto.ExecuteResult
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import org.springframework.stereotype.Component

/**
 * 正常任务
 * <a href="https://juejin.cn/post/7418074037392293914"></a>
 *
 * @author 老马
 */
@Component
@JobExecutor(name = "testJobExecutor")
class TestAnnoJobExecutor {

    fun jobExecute(jobArgs: JobArgs): ExecuteResult {
        SnailJobLog.LOCAL.info("testJobExecutor. jobArgs:{}", JsonUtils.toJsonString(jobArgs))
        SnailJobLog.REMOTE.info("testJobExecutor. jobArgs:{}", JsonUtils.toJsonString(jobArgs))
        return ExecuteResult.success("测试成功")
    }
}
