package com.github.alphafoxz.foxden.common.core.factory

import cn.hutool.core.lang.PatternPool
import com.github.alphafoxz.foxden.common.core.constant.RegexConstants
import java.util.regex.Pattern

/**
 * 正则表达式模式池工厂
 * <p>初始化的时候将正则表达式加入缓存池当中</p>
 * <p>提高正则表达式的性能，避免重复编译相同的正则表达式</p>
 */
object RegexPatternPoolFactory : PatternPool() {
    /**
     * 字典类型必须以字母开头，且只能为（小写字母，数字，下滑线）
     */
    val DICTIONARY_TYPE: Pattern = get(RegexConstants.DICTIONARY_TYPE)

    /**
     * 身份证号码（后6位）
     */
    val ID_CARD_LAST_6: Pattern = get(RegexConstants.ID_CARD_LAST_6)

    /**
     * QQ号码
     */
    val QQ_NUMBER: Pattern = get(RegexConstants.QQ_NUMBER)

    /**
     * 邮政编码
     */
    val POSTAL_CODE: Pattern = get(RegexConstants.POSTAL_CODE)

    /**
     * 注册账号
     */
    val ACCOUNT: Pattern = get(RegexConstants.ACCOUNT)

    /**
     * 密码：包含至少8个字符，包括大写字母、小写字母、数字和特殊字符
     */
    val PASSWORD: Pattern = get(RegexConstants.PASSWORD)

    /**
     * 通用状态（0表示正常，1表示停用）
     */
    val STATUS: Pattern = get(RegexConstants.STATUS)
}
