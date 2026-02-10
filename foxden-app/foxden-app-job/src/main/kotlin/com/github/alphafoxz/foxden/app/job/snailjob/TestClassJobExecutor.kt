package com.github.alphafoxz.foxden.app.job.snailjob

import com.aizuda.snailjob.client.job.core.dto.JobArgs
import com.aizuda.snailjob.client.job.core.executor.AbstractJobExecutor
import com.aizuda.snailjob.model.dto.ExecuteResult
import org.springframework.stereotype.Component

/**
 * 继承式任务执行器
 * 通过继承 AbstractJobExecutor 实现任务
 *
 * @author opensnail
 */
@Component
class TestClassJobExecutor : AbstractJobExecutor() {

    override fun doJobExecute(jobArgs: JobArgs): ExecuteResult {
        return ExecuteResult.success("TestClassJobExecutor测试成功")
    }
}
