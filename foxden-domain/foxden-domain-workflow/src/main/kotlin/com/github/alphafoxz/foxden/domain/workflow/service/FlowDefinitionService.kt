package com.github.alphafoxz.foxden.domain.workflow.service

import com.github.alphafoxz.foxden.common.jimmer.core.page.PageQuery
import com.github.alphafoxz.foxden.common.jimmer.core.page.TableDataInfo
import com.github.alphafoxz.foxden.domain.workflow.bo.FlowDefinitionBo
import com.github.alphafoxz.foxden.domain.workflow.vo.FlowDefinitionVo

/**
 * 流程定义Service接口
 *
 * @author AprilWind
 */
interface FlowDefinitionService {

    /**
     * 查询流程定义列表
     *
     * @param flowCode 流程编码
     * @param flowName 流程名称
     * @param pageQuery 分页参数
     * @return 流程定义分页列表
     */
    fun queryList(flowCode: String?, flowName: String?, pageQuery: PageQuery): TableDataInfo<FlowDefinitionVo>

    /**
     * 查询未发布的流程定义列表
     *
     * @param flowCode 流程编码
     * @param flowName 流程名称
     * @param pageQuery 分页参数
     * @return 流程定义分页列表
     */
    fun unPublishList(flowCode: String?, flowName: String?, pageQuery: PageQuery): TableDataInfo<FlowDefinitionVo>

    /**
     * 根据ID查询流程定义
     *
     * @param id 流程定义id
     * @return 流程定义
     */
    fun queryById(id: Long): FlowDefinitionVo?

    /**
     * 发布流程定义
     *
     * @param id 流程定义id
     * @return 结果
     */
    fun publish(id: Long): Boolean

    /**
     * 导出流程定义
     *
     * @param id 流程定义id
     * @return 流程定义JSON
     */
    fun exportDef(id: Long): String?

    /**
     * 导入流程定义
     *
     * @param flowJson 流程定义JSON
     * @param category 分类
     * @return 结果
     */
    fun importJson(flowJson: String, category: String?): Boolean

    /**
     * 删除流程定义
     *
     * @param ids 流程定义id列表
     * @return 结果
     */
    fun removeDef(ids: List<Long>): Boolean

    /**
     * 新增流程定义
     *
     * @param bo 流程定义业务对象
     * @return 结果
     */
    fun insertByBo(bo: FlowDefinitionBo): Boolean

    /**
     * 修改流程定义
     *
     * @param bo 流程定义业务对象
     * @return 结果
     */
    fun updateByBo(bo: FlowDefinitionBo): Boolean

    /**
     * 取消发布流程定义
     *
     * @param id 流程定义id
     * @return 结果
     */
    fun unPublish(id: Long): Boolean

    /**
     * 复制流程定义
     *
     * @param id 流程定义id
     * @return 结果
     */
    fun copy(id: Long): Boolean
}
