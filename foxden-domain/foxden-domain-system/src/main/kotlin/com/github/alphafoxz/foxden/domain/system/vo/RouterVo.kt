package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 路由视图对象
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class RouterVo(
    /**
     * 路由名字
     */
    var name: String? = null,

    /**
     * 路由地址
     */
    var path: String? = null,

    /**
     * 是否隐藏
     */
    var hidden: Boolean? = null,

    /**
     * 组件路径
     */
    var component: String? = null,

    /**
     * 路由参数
     */
    var query: Map<String, Any>? = null,

    /**
     * 重定向地址
     */
    var redirect: String? = null,

    /**
     * 路由别名
     */
    var alias: String? = null,

    /**
     * 其他元数据
     */
    var meta: MetaVo? = null,

    /**
     * 子路由
     */
    var children: List<RouterVo>? = null,

    /**
     * 是否总是显示
     */
    var alwaysShow: Boolean? = null
)

/**
 * 路由元数据信息
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class MetaVo(
    /**
     * 设置该路由在侧边栏和面包屑中展示的名字
     */
    var title: String? = null,

    /**
     * 设置该路由在侧边栏和面包屑中展示的图标
     */
    var icon: String? = null,

    /**
     * 设置为true，则不会被keep-alive缓存
     */
    var noCache: Boolean? = null,

    /**
     * 固定在tab上
     */
    var affix: Boolean? = null
)
