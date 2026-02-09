package com.github.alphafoxz.foxden.app.system.controller

import cn.hutool.core.io.FileUtil
import cn.hutool.crypto.digest.BCrypt
import com.github.alphafoxz.foxden.common.core.domain.R
import com.github.alphafoxz.foxden.common.core.utils.StringUtils
import com.github.alphafoxz.foxden.common.core.utils.file.MimeTypeUtils
import com.github.alphafoxz.foxden.common.encrypt.annotation.ApiEncrypt
import com.github.alphafoxz.foxden.common.idempotent.annotation.RepeatSubmit
import com.github.alphafoxz.foxden.common.jimmer.helper.DataPermissionHelper
import com.github.alphafoxz.foxden.common.log.annotation.Log
import com.github.alphafoxz.foxden.common.log.enums.BusinessType
import com.github.alphafoxz.foxden.common.security.utils.LoginHelper
import com.github.alphafoxz.foxden.common.web.core.BaseController
import com.github.alphafoxz.foxden.domain.system.bo.SysUserBo
import com.github.alphafoxz.foxden.domain.system.bo.SysUserPasswordBo
import com.github.alphafoxz.foxden.domain.system.bo.SysUserProfileBo
import com.github.alphafoxz.foxden.domain.system.service.SysOssService
import com.github.alphafoxz.foxden.domain.system.service.SysUserService
import com.github.alphafoxz.foxden.domain.system.vo.ProfileUserVo
import com.github.alphafoxz.foxden.domain.system.vo.SysOssVo
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * 个人信息业务处理
 *
 * @author FoxDen Team
 */
@Validated
@RestController
@RequestMapping("/system/user/profile")
class SysProfileController(
    private val userService: SysUserService,
    private val ossService: SysOssService
) : BaseController() {

    /**
     * 个人信息
     */
    @GetMapping
    fun profile(): R<ProfileVo> {
        val userId = LoginHelper.getUserId() ?: return R.fail("获取用户信息失败")
        val user = userService.selectUserById(userId)
            ?: return R.fail("获取用户信息失败")

        val roleGroup = userService.selectUserRoleGroup(user.userId!!)
        val postGroup = userService.selectUserPostGroup(user.userId!!)

        // 单独做一个vo专门给个人中心用 避免数据被脱敏
        val profileUser = ProfileUserVo(
            userId = user.userId,
            userName = user.userName,
            nickName = user.nickName,
            email = user.email,
            phonenumber = user.phonenumber,
            sex = user.sex,
            avatar = null, // ProfileUserVo.avatar 是 OSS ID (Long)，不是 URL
            deptId = user.deptId,
            deptName = user.deptName
        )
        return R.ok(ProfileVo(profileUser, roleGroup, postGroup))
    }

    /**
     * 修改用户信息
     */
    @RepeatSubmit
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    fun updateProfile(@Valid @RequestBody profile: SysUserProfileBo): R<Void> {
        val userId = LoginHelper.getUserId() ?: return R.fail("获取用户信息失败")
        val user = SysUserBo().apply {
            this.userId = userId
            nickName = profile.nickName
            phonenumber = profile.phonenumber
            email = profile.email
            sex = profile.sex
        }
        val username = LoginHelper.getUsername()
        if (!profile.phonenumber.isNullOrEmpty() && !userService.checkPhoneUnique(user)) {
            return R.fail("修改用户'$username'失败，手机号码已存在")
        }
        if (!profile.email.isNullOrEmpty() && !userService.checkEmailUnique(user)) {
            return R.fail("修改用户'$username'失败，邮箱账号已存在")
        }
        val rows = DataPermissionHelper.ignore(java.util.function.Supplier { userService.updateUserProfile(user) })
        return if ((rows ?: 0) > 0) R.ok() else R.fail("修改个人信息异常，请联系管理员")
    }

    /**
     * 重置密码
     *
     * @param bo 新旧密码
     */
    @RepeatSubmit
    @ApiEncrypt
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    fun updatePwd(@Valid @RequestBody bo: SysUserPasswordBo): R<Void> {
        val userId = LoginHelper.getUserId() ?: return R.fail("获取用户信息失败")
        val user = userService.selectUserById(userId)
            ?: return R.fail("获取用户信息失败")
        val password = user.password ?: return R.fail("获取用户密码信息失败")
        if (!BCrypt.checkpw(bo.oldPassword, password)) {
            return R.fail("修改密码失败，旧密码错误")
        }
        if (BCrypt.checkpw(bo.newPassword, password)) {
            return R.fail("新密码不能与旧密码相同")
        }
        val rows = DataPermissionHelper.ignore(java.util.function.Supplier {
            userService.resetUserPwd(user.userId!!, BCrypt.hashpw(bo.newPassword))
        })
        return if ((rows ?: 0) > 0) R.ok() else R.fail("修改密码异常，请联系管理员")
    }

    /**
     * 头像上传
     *
     * @param avatarfile 用户头像
     */
    @RepeatSubmit
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping(value = ["/avatar"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun avatar(@RequestPart("avatarfile") avatarfile: MultipartFile): R<AvatarVo> {
        if (!avatarfile.isEmpty) {
            val extension = FileUtil.extName(avatarfile.originalFilename)
            if (!StringUtils.inStringIgnoreCase(extension, *MimeTypeUtils.IMAGE_EXTENSION)) {
                return R.fail("文件格式不正确，请上传${MimeTypeUtils.IMAGE_EXTENSION.contentToString()}格式")
            }
            val oss = ossService.upload(avatarfile)
            if (oss != null) {
                val avatar = oss.url
                val userId = LoginHelper.getUserId() ?: return R.fail("获取用户信息失败")
                val updateSuccess = DataPermissionHelper.ignore(java.util.function.Supplier {
                    userService.updateUserAvatar(userId, oss.ossId!!)
                })
                if (updateSuccess == true) {
                    return R.ok(AvatarVo(avatar))
                }
            }
        }
        return R.fail("上传图片异常，请联系管理员")
    }

    /**
     * 用户头像信息
     *
     * @param imgUrl 头像地址
     */
    data class AvatarVo(val imgUrl: String?)

    /**
     * 用户个人信息
     *
     * @param user 用户信息
     * @param roleGroup 用户所属角色组
     * @param postGroup 用户所属岗位组
     */
    data class ProfileVo(
        val user: ProfileUserVo,
        val roleGroup: String,
        val postGroup: String
    )
}
