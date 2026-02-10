package com.github.alphafoxz.foxden.app.job.snailjob

import cn.hutool.core.thread.ThreadUtil
import com.aizuda.snailjob.client.job.core.MapHandler
import com.aizuda.snailjob.client.job.core.annotation.JobExecutor
import com.aizuda.snailjob.client.job.core.annotation.MapExecutor
import com.aizuda.snailjob.client.job.core.annotation.ReduceExecutor
import com.aizuda.snailjob.client.job.core.dto.MapArgs
import com.aizuda.snailjob.client.job.core.dto.ReduceArgs
import com.aizuda.snailjob.common.log.SnailJobLog
import com.aizuda.snailjob.model.dto.ExecuteResult
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import java.util.stream.IntStream

/**
 * MapReduce任务 动态分配 分片后合并结果
 * <a href="https://juejin.cn/post/7448551286506913802"></a>
 *
 * @author 老马
 */
@Component
@JobExecutor(name = "testMapReduceAnnotation1")
class TestMapReduceJob {

    @MapExecutor
    fun rootMapExecute(mapArgs: MapArgs, mapHandler: MapHandler<Any>): ExecuteResult {
        val partitionSize = 50
        val partition = IntStream.rangeClosed(1, 200)
            .boxed()
            .collect(Collectors.groupingBy { i -> (i - 1) / partitionSize })
            .values
            .stream()
            .toList()

        val serverPort = SpringUtils.getProperty("server.port")
        SnailJobLog.REMOTE.info("端口:{}完成分配任务", serverPort)
        @Suppress("UNCHECKED_CAST")
        return mapHandler.doMap(partition as List<Any>, "doCalc")
    }

    @MapExecutor(taskName = "doCalc")
    fun doCalc(mapArgs: MapArgs): ExecuteResult {
        val sourceList = mapArgs.mapResult as List<Int>
        // 遍历sourceList的每一个元素,计算出一个累加值partitionTotal
        val partitionTotal = sourceList.stream().mapToInt { i -> i }.sum()
        // 打印日志到服务器
        ThreadUtil.sleep(3, TimeUnit.SECONDS)
        val serverPort = SpringUtils.getProperty("server.port")
        SnailJobLog.REMOTE.info("端口:{}, partitionTotal:{}", serverPort, partitionTotal)
        return ExecuteResult.success(partitionTotal)
    }

    @ReduceExecutor
    fun reduceExecute(reduceArgs: ReduceArgs): ExecuteResult {
        val reduceTotal = reduceArgs.mapResult.stream()
            .mapToInt { i -> Integer.parseInt(i as String) }
            .sum()
        val serverPort = SpringUtils.getProperty("server.port")
        SnailJobLog.REMOTE.info("端口:{}, reduceTotal:{}", serverPort, reduceTotal)
        return ExecuteResult.success(reduceTotal)
    }
}
