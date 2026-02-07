package com.github.alphafoxz.foxden.common.core.utils.regex

import cn.hutool.core.exceptions.ValidateException
import cn.hutool.core.lang.Validator
import com.github.alphafoxz.foxden.common.core.factory.RegexPatternPoolFactory
import java.util.regex.Pattern

/**
 * 正则字段校验器
 * 主要验证字段非空、是否为满足指定格式等
 */
object RegexValidator : Validator() {
    /**
     * 字典类型必须以字母开头，且只能为（小写字母，数字，下滑线）
     */
    val DICTIONARY_TYPE: Pattern = RegexPatternPoolFactory.DICTIONARY_TYPE

    /**
     * 身份证号码（后6位）
     */
    val ID_CARD_LAST_6: Pattern = RegexPatternPoolFactory.ID_CARD_LAST_6

    /**
     * QQ号码
     */
    val QQ_NUMBER: Pattern = RegexPatternPoolFactory.QQ_NUMBER

    /**
     * 邮政编码
     */
    val POSTAL_CODE: Pattern = RegexPatternPoolFactory.POSTAL_CODE

    /**
     * 注册账号
     */
    val ACCOUNT: Pattern = RegexPatternPoolFactory.ACCOUNT

    /**
     * 密码：包含至少8个字符，包括大写字母、小写字母、数字和特殊字符
     */
    val PASSWORD: Pattern = RegexPatternPoolFactory.PASSWORD

    /**
     * 通用状态（0表示正常，1表示停用）
     */
    val STATUS: Pattern = RegexPatternPoolFactory.STATUS

    /**
     * 检查输入的账号是否匹配预定义的规则
     */
    fun isAccount(value: CharSequence?): Boolean {
        return isMatchRegex(ACCOUNT, value)
    }

    /**
     * 验证输入的账号是否符合规则，如果不符合，则抛出 ValidateException 异常
     */
    fun <T : CharSequence> validateAccount(value: T, errorMsg: String): T {
        if (!isAccount(value)) {
            throw ValidateException(errorMsg)
        }
        return value
    }

    /**
     * 检查输入的状态是否匹配预定义的规则
     */
    fun isStatus(value: CharSequence?): Boolean {
        return isMatchRegex(STATUS, value)
    }

    /**
     * 验证输入的状态是否符合规则，如果不符合，则抛出 ValidateException 异常
     */
    fun <T : CharSequence> validateStatus(value: T, errorMsg: String): T {
        if (!isStatus(value)) {
            throw ValidateException(errorMsg)
        }
        return value
    }
}
