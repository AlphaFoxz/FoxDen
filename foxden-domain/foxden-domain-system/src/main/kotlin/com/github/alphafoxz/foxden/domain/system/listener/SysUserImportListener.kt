package com.github.alphafoxz.foxden.domain.system.listener

import cn.hutool.core.util.ObjectUtil
import cn.hutool.crypto.digest.BCrypt
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.event.AnalysisEventListener
import com.github.alphafoxz.foxden.common.core.exception.ServiceException
import com.github.alphafoxz.foxden.common.core.utils.MapstructUtils
import com.github.alphafoxz.foxden.common.core.utils.SpringUtils
import com.github.alphafoxz.foxden.common.core.utils.ValidatorUtils
import com.github.alphafoxz.foxden.common.excel.core.ExcelListener
import com.github.alphafoxz.foxden.common.excel.core.ExcelResult
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.domain.system.bo.SysUserBo
import com.github.alphafoxz.foxden.domain.system.service.SysConfigService
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import com.github.alphafoxz.foxden.domain.system.vo.SysUserImportVo
import com.github.alphafoxz.foxden.domain.system.vo.SysUserVo
import org.slf4j.LoggerFactory
import org.springframework.web.util.HtmlUtils

/**
 * 系统用户自定义导入
 *
 * @author Lion Li
 */
class SysUserImportListener(
    private val isUpdateSupport: Boolean
) : ExcelListener<SysUserImportVo> {

    companion object {
        private const val serialVersionUID = 1L
    }

    private val log = LoggerFactory.getLogger(SysUserImportListener::class.java)

    private val userService: SysUserService by lazy { SpringUtils.getBean(SysUserService::class.java) }
    private val configService: SysConfigService by lazy { SpringUtils.getBean(SysConfigService::class.java) }

    private val password: String
    private val operUserId: Long?

    private val dataList = mutableListOf<SysUserImportVo>()
    private val errorList = mutableListOf<String>()

    private var successNum = 0
    private var failureNum = 0
    private val successMsg = StringBuilder()
    private val failureMsg = StringBuilder()

    init {
        // 获取初始密码配置
        val initPassword = configService.selectConfigValueByKey("sys.user.initPassword", "123456")
        this.password = BCrypt.hashpw(initPassword)
        this.operUserId = LoginHelper.getUserId()
    }

    override fun invoke(userVo: SysUserImportVo, context: AnalysisContext) {
        val userName = userVo.userName ?: run {
            failureNum++
            val msg = "<br/>$failureNum、导入失败：用户名不能为空"
            failureMsg.append(msg)
            return
        }

        val sysUser = userService.selectUserByUserName(userName)
        try {
            // 验证是否存在这个用户
            if (sysUser == null) {
                // 新增用户
                val user = MapstructUtils.convert(userVo, SysUserBo::class.java) ?: return
                ValidatorUtils.validate(user)
                user.password = password
                user.createBy = operUserId.toString()
                userService.insertUser(user)
                successNum++
                successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.userName).append(" 导入成功")
            } else if (isUpdateSupport) {
                // 更新用户
                val userId = sysUser.userId ?: return
                val user = MapstructUtils.convert(userVo, SysUserBo::class.java) ?: return
                user.userId = userId
                ValidatorUtils.validate(user)
                userService.checkUserAllowed(userId)
                userService.checkUserDataScope(userId)
                user.updateBy = operUserId.toString()
                userService.updateUser(user)
                successNum++
                successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.userName).append(" 更新成功")
            } else {
                // 用户已存在
                failureNum++
                failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(sysUser.userName).append(" 已存在")
            }
        } catch (e: Exception) {
            failureNum++
            val msg = "<br/>" + failureNum + "、账号 " + HtmlUtils.htmlEscape(userName) + " 导入失败："
            val message = e.message ?: "未知错误"
            failureMsg.append(msg).append(message)
            log.error(msg, e)
        }
    }

    override fun doAfterAllAnalysed(context: AnalysisContext) {
    }

    override fun getExcelResult(): ExcelResult<SysUserImportVo> {
        return object : ExcelResult<SysUserImportVo> {
            override val list: List<SysUserImportVo>
                get() = dataList

            override val errorList: List<String>
                get() = errorList

            override fun getAnalysis(): String {
                if (failureNum > 0) {
                    failureMsg.insert(0, "很抱歉，导入失败！共 $failureNum 条数据格式不正确，错误如下：")
                    throw ServiceException(failureMsg.toString())
                } else {
                    successMsg.insert(0, "恭喜您，数据已全部导入成功！共 $successNum 条，数据如下：")
                }
                return successMsg.toString()
            }
        }
    }
}
