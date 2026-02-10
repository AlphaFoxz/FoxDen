package com.github.alphafoxz.foxden.domain.workflow.service

import com.github.alphafoxz.foxden.common.core.domain.dto.UserDTO

/**
 * 通用工作流服务接口
 *
 * @author AprilWind
 */
interface FlowCommonService {

    /**
     * 发送消息
     *
     * @param flowName    流程定义名称
     * @param instId      实例id
     * @param messageType 消息类型
     * @param message     消息内容，为空则发送默认配置的消息内容
     */
    fun sendMessage(flowName: String, instId: Long, messageType: List<String>, message: String?)

    /**
     * 发送消息
     *
     * @param messageType 消息类型
     * @param message     消息内容
     * @param subject     邮件标题
     * @param userList    接收用户
     */
    fun sendMessage(messageType: List<String>, message: String, subject: String, userList: List<UserDTO>)

    /**
     * 申请人节点编码
     *
     * @param definitionId 流程定义id
     * @return 申请人节点编码
     */
    fun applyNodeCode(definitionId: Long): String
}
