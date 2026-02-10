package com.github.alphafoxz.foxden.app.job.entity

import java.math.BigDecimal

/**
 * 账单DTO
 */
data class BillDto(
    /** 账单ID */
    var billId: Long? = null,

    /** 账单渠道 */
    var billChannel: String? = null,

    /** 账单日期 */
    var billDate: String? = null,

    /** 账单金额 */
    var billAmount: BigDecimal? = null
)
