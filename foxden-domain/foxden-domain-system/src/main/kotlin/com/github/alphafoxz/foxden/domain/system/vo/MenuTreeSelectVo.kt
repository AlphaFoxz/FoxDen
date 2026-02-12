package com.github.alphafoxz.foxden.domain.system.vo

import cn.hutool.core.lang.tree.Tree
import com.fasterxml.jackson.annotation.JsonInclude
import java.io.Serializable

/**
 * 角色菜单树选择VO
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class MenuTreeSelectVo(
    /**
     * 选中菜单ID列表
     */
    val checkedKeys: List<Long>,

    /**
     * 菜单树列表
     */
    val menus: List<Tree<Long>>
) : Serializable
