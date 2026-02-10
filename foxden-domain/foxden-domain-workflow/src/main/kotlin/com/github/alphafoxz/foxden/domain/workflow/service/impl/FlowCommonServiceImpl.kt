package com.github.alphafoxz.foxden.domain.workflow.service.impl

import com.github.alphafoxz.foxden.common.core.domain.dto.UserDTO
import com.github.alphafoxz.foxden.domain.workflow.entity.FoxFlowNode
import com.github.alphafoxz.foxden.domain.workflow.service.FlowCommonService
import org.babyfish.jimmer.sql.kt.KSqlClient
import org.dromara.warm.flow.core.FlowEngine
import org.dromara.warm.flow.core.service.NodeService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 通用工作流服务实现
 *
 * @author AprilWind
 */
@Service
class FlowCommonServiceImpl(
    private val sqlClient: KSqlClient
) : FlowCommonService {

    private val log = LoggerFactory.getLogger(FlowCommonServiceImpl::class.java)

    // WarmFlow service
    private val nodeService: NodeService = FlowEngine.nodeService()

    /**
     * 发送消息
     *
     * @param flowName    流程定义名称
     * @param instId      实例id
     * @param messageType 消息类型（1:站内信, 2:邮件, 3:短信）
     * @param message     消息内容，为空则发送默认配置的消息内容
     */
    override fun sendMessage(flowName: String, instId: Long, messageType: List<String>, message: String?) {
        // TODO: 需要实现消息发送逻辑
        // 业务逻辑：
        // 1. 根据实例ID查询任务信息：
        //    - 查询 flow_task 表获取当前节点的任务
        //    - 获取任务关联的用户（通过 flow_user 表）
        // 2. 获取任务处理人列表
        // 3. 根据 messageType 发送不同类型的消息：
        //    - "1": 站内信 - 写入 sys_notice 表，设置通知类型为流程通知
        //    - "2": 邮件 - 调用 MailUtils.sendMail() 发送邮件
        //    - "3": 短信 - 调用短信服务发送短信
        //    - 支持同时发送多种类型（如 "1,2" 同时发送站内信和邮件）
        // 4. 如果 message 为空，使用默认消息模板：
        //    - "您有一个新的【{flowName}】流程待处理，请及时查看"
        // 5. 记录发送结果到日志
        log.info("发送消息 - 流程: {}, 实例ID: {}, 消息类型: {}, 消息内容: {}",
            flowName, instId, messageType, message)
    }

    /**
     * 发送消息
     *
     * @param messageType 消息类型（1:站内信, 2:邮件, 3:短信）
     * @param message     消息内容
     * @param subject     邮件标题（仅用于邮件类型）
     * @param userList    接收用户列表
     */
    override fun sendMessage(messageType: List<String>, message: String, subject: String, userList: List<UserDTO>) {
        // TODO: 需要实现消息发送逻辑
        // 业务逻辑：
        // 1. 遍历 messageType 列表，根据类型发送消息：
        //
        //    站内信 (messageType = "1")：
        //    - 遍历 userList，为每个用户创建通知记录
        //    - 写入 sys_notice 表：
        //      - noticeTitle: subject 或 "流程通知"
        //      - noticeContent: message
        //      - noticeType: "2" (通知类型：2=流程通知)
        //      - status: "0" (未读状态)
        //      - createBy: 当前用户ID
        //      - receiver: 接收人ID
        //
        //    邮件 (messageType = "2")：
        //    - 调用 MailUtils.sendMail() 发送邮件
        //    - 参数：
        //      - to: userList 中所有用户的邮箱地址
        //      - subject: 邮件标题
        //      - content: 邮件内容（支持 HTML）
        //      - isHtml: true
        //
        //    短信 (messageType = "3")：
        //    - 调用短信服务发送短信
        //    - 遍历 userList，为每个用户发送短信
        //    - 使用用户的手机号（phonenumber 字段）
        //
        // 2. 记录发送结果（成功/失败数量）
        log.info("发送消息 - 消息类型: {}, 邮件标题: {}, 消息内容: {}, 接收用户数: {}",
            messageType, subject, message, userList.size)
    }

    /**
     * 申请人节点编码
     *
     * @param definitionId 流程定义id
     * @return 申请人节点编码
     */
    override fun applyNodeCode(definitionId: Long): String {
        // 根据 definitionId 查询流程定义的所有节点
        val nodes = sqlClient.createQuery(FoxFlowNode::class) {
            select(table)
        }.execute().filter { it.definitionId == definitionId }

        // 获取第一个节点（节点序号最小的）
        return nodes
            .minByOrNull { it.nodeRatio?.toIntOrNull() ?: 0 }
            ?.nodeCode ?: "apply"
    }
}
