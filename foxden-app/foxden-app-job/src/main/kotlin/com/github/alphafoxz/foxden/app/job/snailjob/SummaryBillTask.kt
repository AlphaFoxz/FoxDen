package com.github.alphafoxz.foxden.app.job.snailjob

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
 * DAG工作流任务-模拟汇总账单任务
 * <a href="https://juejin.cn/post/7487860254114644019"></a>
 *
 * @author opensnail
 */
@Component
@JobExecutor(name = "summaryBillTask")
class SummaryBillTask {

    fun jobExecute(jobArgs: JobArgs): ExecuteResult {
        // 获得微信账单
        var wechatAmount = BigDecimal.ZERO
        val wechat = jobArgs.wfContext["wechat"] as? String
        if (StrUtil.isNotBlank(wechat)) {
            val wechatBillDto = JsonUtils.parseObject(wechat, BillDto::class.java)
            wechatAmount = wechatBillDto?.billAmount ?: BigDecimal.ZERO
        }
        // 获得支付宝账单
        var alipayAmount = BigDecimal.ZERO
        val alipay = jobArgs.wfContext["alipay"] as? String
        if (StrUtil.isNotBlank(alipay)) {
            val alipayBillDto = JsonUtils.parseObject(alipay, BillDto::class.java)
            alipayAmount = alipayBillDto?.billAmount ?: BigDecimal.ZERO
        }
        // 汇总账单
        val totalAmount = wechatAmount.add(alipayAmount)
        SnailJobLog.REMOTE.info("总金额: {}", totalAmount)
        return ExecuteResult.success(totalAmount)
    }
}
