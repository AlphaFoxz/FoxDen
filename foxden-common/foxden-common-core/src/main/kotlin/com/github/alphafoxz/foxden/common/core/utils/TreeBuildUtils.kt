package com.github.alphafoxz.foxden.common.core.utils

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.lang.tree.Tree
import cn.hutool.core.lang.tree.TreeNodeConfig
import cn.hutool.core.lang.tree.TreeUtil
import cn.hutool.core.lang.tree.parser.NodeParser
import com.github.alphafoxz.foxden.common.core.utils.reflect.ReflectUtils
import java.util.stream.Collectors

/**
 * 扩展 hutool TreeUtil 封装系统树构建
 */
object TreeBuildUtils {
    /**
     * 根据前端定制差异化字段
     */
    val DEFAULT_CONFIG: TreeNodeConfig = TreeNodeConfig.DEFAULT_CONFIG.setNameKey("label")

    /**
     * 构建树形结构
     */
    @JvmStatic
    fun <T, K> build(list: List<T>?, nodeParser: NodeParser<T, K>): List<Tree<K>> {
        if (CollUtil.isEmpty(list)) return CollUtil.newArrayList()
        val k: K = ReflectUtils.invokeGetter(list!![0], "parentId") ?: return CollUtil.newArrayList()
        return TreeUtil.build(list, k, DEFAULT_CONFIG, nodeParser)
    }

    /**
     * 构建树形结构
     */
    @JvmStatic
    fun <T, K> build(list: List<T>?, parentId: K, nodeParser: NodeParser<T, K>): List<Tree<K>> {
        if (CollUtil.isEmpty(list)) return CollUtil.newArrayList()
        return TreeUtil.build(list, parentId, DEFAULT_CONFIG, nodeParser)
    }

    /**
     * 构建多根节点的树结构（支持多个顶级节点）
     */
    @JvmStatic
    fun <T, K> buildMultiRoot(
        list: List<T>?,
        getId: java.util.function.Function<T, K>,
        getParentId: java.util.function.Function<T, K>,
        parser: NodeParser<T, K>
    ): List<Tree<K>> {
        if (CollUtil.isEmpty(list)) return CollUtil.newArrayList()

        val rootParentIds = StreamUtils.toSet(list, getParentId).toMutableSet()
        val allIds = StreamUtils.toSet(list, getId)
        rootParentIds.removeAll(allIds)

        return rootParentIds.stream()
            .flatMap { rootParentId -> TreeUtil.build(list, rootParentId, parser).stream() }
            .collect(Collectors.toList())
    }

    /**
     * 获取节点列表中所有节点的叶子节点
     */
    @JvmStatic
    fun <K> getLeafNodes(nodes: List<Tree<K>>?): List<Tree<K>> {
        if (CollUtil.isEmpty(nodes)) return CollUtil.newArrayList()
        return nodes!!.stream()
            .flatMap { node -> extractLeafNodes(node).stream() }
            .collect(Collectors.toList())
    }

    /**
     * 获取指定节点下的所有叶子节点
     */
    private fun <K> extractLeafNodes(node: Tree<K>): List<Tree<K>> {
        if (!node.hasChild()) return listOf(node)
        return node.children.stream()
            .flatMap { child -> extractLeafNodes(child).stream() }
            .collect(Collectors.toList())
    }
}
