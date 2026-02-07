package com.github.alphafoxz.foxden.common.excel.core

import cn.hutool.core.convert.Convert
import cn.hutool.core.util.StrUtil
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import java.util.function.Function

/**
 * Excel下拉可选项
 * 注意：为确保下拉框解析正确，传值务必使用createOptionValue()做为值的拼接
 *
 * @author Emil.Zhang
 */
data class DropDownOptions(
    /**
     * 一级下拉所在列index，从0开始算
     */
    var index: Int = 0,

    /**
     * 二级下拉所在的index，从0开始算，不能与一级相同
     */
    var nextIndex: Int = 0,

    /**
     * 一级下拉所包含的数据
     */
    var options: MutableList<String> = mutableListOf(),

    /**
     * 二级下拉所包含的数据Map
     * 以每一个一级选项值为Key，每个一级选项对应的二级数据为Value
     */
    var nextOptions: MutableMap<String, List<String>> = mutableMapOf()
) {
    companion object {
        private const val DELIMITER = "_"

        /**
         * 创建只有一级的下拉选
         */
        fun of(index: Int, options: List<String>): DropDownOptions {
            return DropDownOptions(index = index, options = options.toMutableList())
        }

        /**
         * 创建每个选项可选值
         * 注意：不能以数字，特殊符号开头，选项中不可以包含任何运算符号
         *
         * @param vars 可选值内包含的参数
         * @return 合规的可选值
         */
        @JvmStatic
        fun createOptionValue(vararg vars: Any?): String {
            val stringBuffer = StringBuilder()
            val regex = "^[\\S\\d\\u4e00-\\u9fa5]+$".toRegex()
            for (i in vars.indices) {
                val v = StrUtil.trimToEmpty(Convert.toStr(vars[i]))
                if (!v.matches(regex)) {
                    throw ServiceException("选项数据不符合规则，仅允许使用中英文字符以及数字")
                }
                stringBuffer.append(v)
                if (i < vars.size - 1) {
                    // 直至最后一个前，都以_作为切割线
                    stringBuffer.append(DELIMITER)
                }
            }
            if (stringBuffer.toString().matches("^\\d_*$".toRegex())) {
                throw ServiceException("禁止以数字开头")
            }
            return stringBuffer.toString()
        }

        /**
         * 将处理后合理的可选值解析为原始的参数
         *
         * @param option 经过处理后的合理的可选项
         * @return 原始的参数
         */
        @JvmStatic
        fun analyzeOptionValue(option: String): List<String> {
            return StrUtil.split(option, DELIMITER, true, true)
        }

        /**
         * 创建级联下拉选项
         *
         * @param parentList                  父实体可选项原始数据
         * @param parentIndex                 父下拉选位置
         * @param sonList                     子实体可选项原始数据
         * @param sonIndex                    子下拉选位置
         * @param parentHowToGetIdFunction    父类如何获取唯一标识
         * @param sonHowToGetParentIdFunction 子类如何获取父类的唯一标识
         * @param howToBuildEveryOption       如何生成下拉选内容
         * @return 级联下拉选项
         */
        @JvmStatic
        fun <T> buildLinkedOptions(
            parentList: List<T>,
            parentIndex: Int,
            sonList: List<T>,
            sonIndex: Int,
            parentHowToGetIdFunction: Function<T, Number>,
            sonHowToGetParentIdFunction: Function<T, Number>,
            howToBuildEveryOption: Function<T, String>
        ): DropDownOptions {
            val parentLinkSonOptions = DropDownOptions()
            // 先创建父类的下拉
            parentLinkSonOptions.index = parentIndex
            parentLinkSonOptions.options = parentList.map { t -> howToBuildEveryOption.apply(t) }.toMutableList()

            // 提取父-子级联下拉
            val sonOptions = mutableMapOf<String, MutableList<String>>()
            // 父级依据自己的ID分组
            val parentGroupByIdMap = parentList.groupBy { parentHowToGetIdFunction.apply(it) }

            // 遍历每个子集，提取到Map中
            sonList.forEach { everySon ->
                val parentId = sonHowToGetParentIdFunction.apply(everySon)
                if (parentGroupByIdMap.containsKey(parentId)) {
                    // 找到对应的上级
                    val parentObj = parentGroupByIdMap[parentId]!![0]
                    // 提取名称和ID作为Key
                    val key = howToBuildEveryOption.apply(parentObj)
                    // Key对应的Value
                    val thisParentSonOptionList = sonOptions.getOrPut(key) { mutableListOf() }
                    // 往Value中添加当前子集选项
                    thisParentSonOptionList.add(howToBuildEveryOption.apply(everySon))
                }
            }

            parentLinkSonOptions.nextIndex = sonIndex
            parentLinkSonOptions.nextOptions = sonOptions as MutableMap<String, List<String>>
            return parentLinkSonOptions
        }
    }
}
