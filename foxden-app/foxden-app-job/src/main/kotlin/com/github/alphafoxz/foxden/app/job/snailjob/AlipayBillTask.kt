package com.github.alphafoxz.foxden.app.job.snailjob

import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.StrUtil
import com.aizuda.snailjob.client.job.core.annotation.JobExecutor
import com.aizuda.snailjob.client.job.core.dto.JobArgs
import com.aizuda.snailjob.common.log.SnailJobLog
import com.aizuda.snailjob.model.dto.ExecuteResult
import com.github.alphafoxz.foxden.app.job.entity.BillDto
import com.github.alphafoxz.foxden.common.json.utils.JsonUtils
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * DAG工作流任务-模拟支付宝账单任务
 * <a href="https://juejin.cn/post/7487860254114644019"></a>
 *
 * @author opensnail
 */
@Component
@JobExecutor(name = "alipayBillTask")
class AlipayBillTask {

    fun jobExecute(jobArgs: JobArgs): ExecuteResult {
        val billDto = BillDto(
            billId = 23456789L,
            billChannel = "alipay"
        )
        // 设置清算日期
        var settlementDate = jobArgs.wfContext["settlementDate"] as? String
        if (StrUtil.equals(settlementDate, "sysdate")) {
            settlementDate = DateUtil.today()
        }
        billDto.billDate = settlementDate
        billDto.billAmount = BigDecimal("2345.67")
        // 把billDto对象放入上下文进行传递
        jobArgs.appendContext("alipay", JsonUtils.toJsonString(billDto))
        SnailJobLog.REMOTE.info("上下文: {}", jobArgs.wfContext)
        return ExecuteResult.success(billDto)
    }
}
