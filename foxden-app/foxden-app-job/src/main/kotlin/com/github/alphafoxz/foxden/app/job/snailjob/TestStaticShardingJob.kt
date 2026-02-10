package com.github.alphafoxz.foxden.app.job.snailjob

import cn.hutool.core.convert.Convert
import com.aizuda.snailjob.client.job.core.annotation.JobExecutor
import com.aizuda.snailjob.client.job.core.dto.JobArgs
import com.aizuda.snailjob.common.log.SnailJobLog
import com.aizuda.snailjob.model.dto.ExecuteResult
import org.springframework.stereotype.Component

/**
 * 静态分片 根据服务端任务参数分片
 * <a href="https://juejin.cn/post/7426232375703896101"></a>
 *
 * @author 老马
 */
@Component
@JobExecutor(name = "testStaticShardingJob")
class TestStaticShardingJob {

    fun jobExecute(jobArgs: JobArgs): ExecuteResult {
        val jobParams = Convert.toStr(jobArgs.jobParams)
        SnailJobLog.LOCAL.info("开始执行分片任务, 参数:{}", jobParams)
        // 获得jobArgs 中传入的开始id和结束id
        val split = jobParams.split(",")
        val fromId = split[0].toLong()
        val toId = split[1].toLong()
        // 模拟数据库操作,对范围id,进行加密处理
        return try {
            SnailJobLog.REMOTE.info("开始对id范围:{}进行加密处理", "$fromId-$toId")
            Thread.sleep(3000)
            SnailJobLog.REMOTE.info("对id范围:{}进行加密处理完成", "$fromId-$toId")
            ExecuteResult.success("执行分片任务完成")
        } catch (e: InterruptedException) {
            ExecuteResult.failure("任务执行失败")
        }
    }
}
