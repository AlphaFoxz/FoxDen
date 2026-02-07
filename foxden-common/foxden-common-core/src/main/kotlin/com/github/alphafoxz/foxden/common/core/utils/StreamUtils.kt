package com.github.alphafoxz.foxden.common.core.utils

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.map.MapUtil
import java.util.LinkedHashMap
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Collectors

/**
 * Stream 流工具类
 */
object StreamUtils {
    /**
     * 将collection过滤
     */
    @JvmStatic
    fun <E> filter(collection: Collection<E>?, function: Predicate<in E>): List<E> {
        if (CollUtil.isEmpty(collection)) return CollUtil.newArrayList()
        return collection!!.stream()
            .filter(function)
            .collect(Collectors.toList())
    }

    /**
     * 找到流中满足条件的第一个元素
     */
    @JvmStatic
    fun <E> findFirst(collection: Collection<E>?, function: Predicate<in E>): E? {
        if (CollUtil.isEmpty(collection)) return null
        return collection!!.stream()
            .filter(function)
            .findFirst()
            .orElse(null)
    }

    /**
     * 找到流中任意一个满足条件的元素值
     */
    @JvmStatic
    fun <E> findAnyValue(collection: Collection<E>?, function: Predicate<in E>): E? {
        if (CollUtil.isEmpty(collection)) return null
        return collection!!.stream()
            .filter(function)
            .findAny()
            .orElse(null)
    }

    /**
     * 将collection拼接
     */
    @JvmStatic
    fun <E> join(collection: Collection<E>?, function: Function<in E, String?>): String {
        return join(collection, function, StringUtils.SEPARATOR)
    }

    /**
     * 将collection拼接
     */
    @JvmStatic
    fun <E> join(collection: Collection<E>?, function: Function<in E, String?>, delimiter: CharSequence): String {
        if (CollUtil.isEmpty(collection)) return ""
        return collection!!.stream()
            .map(function)
            .filter { obj -> obj != null }
            .collect(Collectors.joining(delimiter))
    }

    /**
     * 将collection排序
     */
    @JvmStatic
    fun <E> sorted(collection: Collection<E>?, comparing: Comparator<in E>): List<E> {
        if (CollUtil.isEmpty(collection)) return CollUtil.newArrayList()
        return collection!!.stream()
            .filter { obj -> obj != null }
            .sorted(comparing)
            .collect(Collectors.toList())
    }

    /**
     * 将collection转化为类型不变的map
     */
    @JvmStatic
    fun <V, K> toIdentityMap(collection: Collection<V>?, key: Function<in V, K>): Map<K, V> {
        if (CollUtil.isEmpty(collection)) return MapUtil.newHashMap()
        return collection!!.stream()
            .filter { obj -> obj != null }
            .collect(Collectors.toMap(key, Function.identity(), { l, _ -> l }))
    }

    /**
     * 将Collection转化为map
     */
    @JvmStatic
    fun <E, K, V> toMap(collection: Collection<E>?, key: Function<in E, K>, value: Function<in E, V>): Map<K, V> {
        if (CollUtil.isEmpty(collection)) return MapUtil.newHashMap()
        return collection!!.stream()
            .filter { obj -> obj != null }
            .collect(Collectors.toMap(key, value, { l, _ -> l }))
    }

    /**
     * 获取 map 中的数据作为新 Map 的 value
     */
    @JvmStatic
    fun <K, E, V> toMap(map: Map<K, E>?, take: BiFunction<K, E, V>): Map<K, V> {
        if (CollUtil.isEmpty(map)) return MapUtil.newHashMap()
        return toMap(map!!.entries, { it.key }) { entry -> take.apply(entry.key, entry.value) }
    }

    /**
     * 将collection按照规则分类成map
     */
    @JvmStatic
    fun <E, K> groupByKey(collection: Collection<E>?, key: Function<in E, K>): Map<K, List<E>> {
        if (CollUtil.isEmpty(collection)) return MapUtil.newHashMap()
        return collection!!.stream()
            .filter { obj -> obj != null }
            .collect(Collectors.groupingBy(key, { LinkedHashMap() }, Collectors.toList()))
    }

    /**
     * 将collection按照两个规则分类成双层map
     */
    @JvmStatic
    fun <E, K, U> groupBy2Key(collection: Collection<E>?, key1: Function<in E, K>, key2: Function<in E, U>): Map<K, Map<U, List<E>>> {
        if (CollUtil.isEmpty(collection)) return MapUtil.newHashMap()
        return collection!!.stream()
            .filter { obj -> obj != null }
            .collect(Collectors.groupingBy(key1, { LinkedHashMap() }, Collectors.groupingBy(key2, { LinkedHashMap() }, Collectors.toList())))
    }

    /**
     * 将collection按照两个规则分类成双层map
     */
    @JvmStatic
    fun <E, T, U> group2Map(collection: Collection<E>?, key1: Function<in E, T>, key2: Function<in E, U>): Map<T, Map<U, E>> {
        if (CollUtil.isEmpty(collection)) return MapUtil.newHashMap()
        return collection!!.stream()
            .filter { obj -> obj != null }
            .collect(Collectors.groupingBy(key1, { LinkedHashMap() }, Collectors.toMap(key2, Function.identity(), { l, _ -> l })))
    }

    /**
     * 将collection转化为List集合
     */
    @JvmStatic
    fun <E, T> toList(collection: Collection<E>?, function: Function<in E, T>): List<T> {
        if (CollUtil.isEmpty(collection)) return CollUtil.newArrayList()
        return collection!!.stream()
            .map(function)
            .filter { obj -> obj != null }
            .collect(Collectors.toList())
    }

    /**
     * 将collection转化为Set集合
     */
    @JvmStatic
    fun <E, T> toSet(collection: Collection<E>?, function: Function<in E, T>): Set<T> {
        if (CollUtil.isEmpty(collection)) return CollUtil.newHashSet()
        return collection!!.stream()
            .map(function)
            .filter { obj -> obj != null }
            .collect(Collectors.toSet())
    }

    /**
     * 合并两个相同key类型的map
     */
    @JvmStatic
    fun <K, X, Y, V> merge(map1: Map<K, X>?, map2: Map<K, Y>?, merge: BiFunction<X?, Y?, V>): Map<K, V> {
        if (CollUtil.isEmpty(map1) && CollUtil.isEmpty(map2)) return MapUtil.newHashMap()
        if (CollUtil.isEmpty(map1)) {
            return toMap(map2!!.entries, { it.key }) { entry -> merge.apply(null, entry.value) }
        }
        if (CollUtil.isEmpty(map2)) {
            return toMap(map1!!.entries, { it.key }) { entry -> merge.apply(entry.value, null) }
        }
        val keySet = HashSet<K>()
        keySet.addAll(map1!!.keys)
        keySet.addAll(map2!!.keys)
        return toMap(keySet, { key -> key }) { key -> merge.apply(map1[key], map2[key]) }
    }
}
