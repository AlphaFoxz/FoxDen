/**
 * FoxDen 用户领域模型
 * 基于 ruoyi 框架的用户管理模块进行 DDD 建模
 */

import {createDomainDesigner} from '@ddd-tool/domain-designer-core'

// 创建领域设计器实例
export const d = createDomainDesigner({moduleName: 'user-domain'})
export const i = d.info

// ============================================
// 值对象定义 (Value Objects)
// ============================================

export const userValues = {
    // 用户ID
    userId: i.id('userId', '用户ID'),

    // 租户ID
    tenantId: i.id('tenantId', '租户ID'),

    // 登录名
    userName: i.valueObj('userName', '用户登录名'),

    // 用户昵称
    nickName: i.valueObj('nickName', '用户昵称'),

    // 邮箱
    email: i.valueObj('email', '用户邮箱'),

    // 手机号
    phonenumber: i.valueObj('phonenumber', '手机号码'),

    // 性别 (0=未知, 1=男, 2=女)
    sex: i.valueObj('sex', '性别'),

    // 头像
    avatar: i.valueObj('avatar', '头像地址'),

    // 密码
    password: i.valueObj('password', '用户密码(BCrypt加密)'),

    // 用户状态 (0=正常, 1=停用)
    status: i.valueObj('status', '用户状态'),

    // 删除标志 (0=存在, 2=删除)
    delFlag: i.valueObj('delFlag', '删除标志'),

    // 最后登录IP
    loginIp: i.valueObj('loginIp', '最后登录IP'),

    // 最后登录时间
    loginDate: i.valueObj('loginDate', '最后登录时间'),

    // 部门ID
    deptId: i.id('deptId', '部门ID'),

    // 岗位ID
    postId: i.id('postId', '岗位ID'),

    // 角色ID
    roleId: i.id('roleId', '角色ID'),

    // 创建部门
    createDept: i.valueObj('createDept', '创建部门'),

    // 创建者
    createBy: i.valueObj('createBy', '创建者'),

    // 创建时间
    createTime: i.valueObj('createTime', '创建时间'),

    // 更新者
    updateBy: i.valueObj('updateBy', '更新者'),

    // 更新时间
    updateTime: i.valueObj('updateTime', '更新时间'),

    // 备注
    remark: i.valueObj('remark', '备注'),

    // 旧密码
    oldPassword: i.valueObj('oldPassword', '旧密码'),

    // 新密码
    newPassword: i.valueObj('newPassword', '新密码'),

    // 验证码
    verifyCode: i.valueObj('verifyCode', '验证码'),

    // 登录IP
    ipAddress: i.valueObj('ipAddress', '登录IP地址'),

    // 登录地点
    loginLocation: i.valueObj('loginLocation', '登录地点'),
}

// ============================================
// 角色领域值对象
// ============================================

export const roleValues = {
    roleId: i.id('roleId', '角色ID'),
    roleName: i.valueObj('roleName', '角色名称'),
    roleKey: i.valueObj('roleKey', '角色权限字符串'),
    roleSort: i.valueObj('roleSort', '显示顺序'),
    dataScope: i.valueObj('dataScope', '数据范围'),
    menuCheckStrictly: i.valueObj('menuCheckStrictly', '菜单树选择项是否关联显示'),
    deptCheckStrictly: i.valueObj('deptCheckStrictly', '部门树选择项是否关联显示'),
    status: i.valueObj('status', '角色状态'),
}

// ============================================
// 部门领域值对象
// ============================================

export const deptValues = {
    deptId: i.id('deptId', '部门ID'),
    parentId: i.id('parentId', '父部门ID'),
    deptName: i.valueObj('deptName', '部门名称'),
    ancestors: i.valueObj('ancestors', '祖级列表'),
    orderNum: i.valueObj('orderNum', '显示顺序'),
    leader: i.valueObj('leader', '负责人'),
    phone: i.valueObj('phone', '联系电话'),
    email: i.valueObj('email', '邮箱'),
    status: i.valueObj('status', '部门状态'),
}

// ============================================
// 岗位领域值对象
// ============================================

export const postValues = {
    postId: i.id('postId', '岗位ID'),
    postCode: i.valueObj('postCode', '岗位编码'),
    postName: i.valueObj('postName', '岗位名称'),
    postSort: i.valueObj('postSort', '显示顺序'),
    status: i.valueObj('status', '状态'),
}

// ============================================
// 聚合根定义 (Aggregates)
// ============================================

// 用户聚合根
export const userAggregate = d.agg(
    'UserAggregate',
    [
        userValues.userId,
        userValues.userName,
        userValues.nickName,
        userValues.email,
        userValues.phonenumber,
        userValues.password,
        userValues.status,
        userValues.delFlag,
        userValues.deptId,
        userValues.tenantId,
    ],
    '用户聚合根',
)

// 角色聚合根
export const roleAggregate = d.agg(
    'RoleAggregate',
    [roleValues.roleId, roleValues.roleName, roleValues.roleKey, roleValues.dataScope],
    '角色聚合根',
)

// 部门聚合根
export const deptAggregate = d.agg(
    'DeptAggregate',
    [deptValues.deptId, deptValues.deptName, deptValues.parentId],
    '部门聚合根',
)

// 岗位聚合根
export const postAggregate = d.agg(
    'PostAggregate',
    [postValues.postId, postValues.postCode, postValues.postName],
    '岗位聚合根',
)

// ============================================
// 命令定义 (Commands)
// ============================================

// 用户注册命令
export const registerUserCmd = d.command(
    'RegisterUser',
    [
        userValues.userName,
        userValues.password,
        userValues.nickName,
        userValues.email,
        userValues.phonenumber,
        userValues.deptId,
    ],
    '用户注册',
)

// 用户登录命令
export const loginUserCmd = d.command(
    'LoginUser',
    [userValues.userName, userValues.password, userValues.verifyCode],
    '用户登录',
)

// 用户登出命令
export const logoutUserCmd = d.command('LogoutUser', [userValues.userId], '用户登出')

// 更新用户信息命令
export const updateUserCmd = d.command(
    'UpdateUser',
    [
        userValues.userId,
        userValues.nickName,
        userValues.email,
        userValues.phonenumber,
        userValues.sex,
        userValues.avatar,
        userValues.deptId,
        userValues.remark,
    ],
    '更新用户信息',
)

// 更新用户状态命令
export const updateUserStatusCmd = d.command(
    'UpdateUserStatus',
    [userValues.userId, userValues.status],
    '更新用户状态',
)

// 重置用户密码命令
export const resetUserPasswordCmd = d.command(
    'ResetUserPassword',
    [userValues.userId, userValues.newPassword],
    '重置用户密码',
)

// 修改用户密码命令
export const changeUserPasswordCmd = d.command(
    'ChangeUserPassword',
    [userValues.userId, userValues.oldPassword, userValues.newPassword],
    '修改用户密码',
)

// 分配用户角色命令
export const assignUserRoleCmd = d.command(
    'AssignUserRole',
    [userValues.userId, roleValues.roleId],
    '分配用户角色',
)

// 批量分配用户角色命令
export const assignUserRolesCmd = d.command(
    'AssignUserRoles',
    [userValues.userId, 'roleIds'],
    '批量分配用户角色',
)

// 分配用户岗位命令
export const assignUserPostCmd = d.command(
    'AssignUserPost',
    [userValues.userId, postValues.postId],
    '分配用户岗位',
)

// 删除用户命令
export const deleteUserCmd = d.command('DeleteUser', [userValues.userId], '删除用户')

// 批量删除用户命令
export const deleteUsersCmd = d.command('DeleteUsers', ['userIds'], '批量删除用户')

// 导入用户命令
export const importUsersCmd = d.command('ImportUsers', ['users'], '导入用户')

// SMS 登录命令
export const smsLoginCmd = d.command(
    'SmsLogin',
    [userValues.phonenumber, userValues.verifyCode],
    '短信验证码登录',
)

// 邮箱登录命令
export const emailLoginCmd = d.command(
    'EmailLogin',
    [userValues.email, userValues.verifyCode],
    '邮箱验证码登录',
)

// 社交登录命令
export const socialLoginCmd = d.command(
    'SocialLogin',
    ['source', 'code', 'state'],
    '社交登录',
)

// 微信小程序登录命令
export const xcxLoginCmd = d.command('XcxLogin', ['code'], '微信小程序登录')

// ============================================
// 事件定义 (Events)
// ============================================

// 用户已创建事件
export const userCreatedEvent = d.event(
    'UserCreated',
    [userValues.userId, userValues.userName, userValues.tenantId, userValues.createTime],
    '用户已创建',
)

// 用户已登录事件
export const userLoggedInEvent = d.event(
    'UserLoggedIn',
    [userValues.userId, userValues.userName, userValues.loginIp, userValues.loginDate],
    '用户已登录',
)

// 用户已登出事件
export const userLoggedOutEvent = d.event(
    'UserLoggedOut',
    [userValues.userId, userValues.userName],
    '用户已登出',
)

// 用户信息已更新事件
export const userUpdatedEvent = d.event(
    'UserUpdated',
    [userValues.userId, userValues.userName, userValues.updateTime],
    '用户信息已更新',
)

// 用户状态已更新事件
export const userStatusUpdatedEvent = d.event(
    'UserStatusUpdated',
    [userValues.userId, userValues.status],
    '用户状态已更新',
)

// 用户密码已重置事件
export const userPasswordResetEvent = d.event(
    'UserPasswordReset',
    [userValues.userId, userValues.userName],
    '用户密码已重置',
)

// 用户密码已修改事件
export const userPasswordChangedEvent = d.event(
    'UserPasswordChanged',
    [userValues.userId, userValues.userName],
    '用户密码已修改',
)

// 用户角色已分配事件
export const userRoleAssignedEvent = d.event(
    'UserRoleAssigned',
    [userValues.userId, roleValues.roleId],
    '用户角色已分配',
)

// 用户岗位已分配事件
export const userPostAssignedEvent = d.event(
    'UserPostAssigned',
    [userValues.userId, postValues.postId],
    '用户岗位已分配',
)

// 用户已删除事件
export const userDeletedEvent = d.event(
    'UserDeleted',
    [userValues.userId, userValues.userName],
    '用户已删除',
)

// 用户已锁定事件
export const userLockedEvent = d.event(
    'UserLocked',
    [userValues.userId, userValues.userName],
    '用户已锁定',
)

// 用户已解锁事件
export const userUnlockedEvent = d.event(
    'UserUnlocked',
    [userValues.userId, userValues.userName],
    '用户已解锁',
)

// 登录失败事件
export const loginFailedEvent = d.event(
    'LoginFailed',
    [userValues.userName, userValues.ipAddress, 'message'],
    '登录失败',
)

// ============================================
// 策略定义 (Policies)
// ============================================

// 密码复杂度策略
export const passwordComplexityPolicy = d.policy(
    'PasswordComplexityPolicy',
    d.note`密码复杂度策略
    1. 密码长度不能少于6位
    2. 必须包含字母和数字
    3. 不能与用户名相同`,
)

// 账号锁定策略
export const accountLockPolicy = d.policy(
    'AccountLockPolicy',
    d.note`账号锁定策略
    1. 连续登录失败5次
    2. 锁定时长10分钟
    3. 锁定期间无法登录`,
)

// 用户名唯一性策略
export const userNameUniquePolicy = d.policy(
    'UserNameUniquePolicy',
    d.note`用户名唯一性策略
    1. 同一租户内用户名唯一
    2. 不区分大小写`,
)

// 邮箱唯一性策略
export const emailUniquePolicy = d.policy(
    'EmailUniquePolicy',
    d.note`邮箱唯一性策略
    1. 同一租户内邮箱唯一
    2. 允许为空`,
)

// 手机号唯一性策略
export const phoneNumberUniquePolicy = d.policy(
    'PhoneNumberUniquePolicy',
    d.note`手机号唯一性策略
    1. 同一租户内手机号唯一
    2. 允许为空`,
)

// 数据权限策略
export const dataPermissionPolicy = d.policy(
    'DataPermissionPolicy',
    d.note`数据权限策略
    1. 全部数据权限
    2. 自定义数据权限
    3. 本部门数据权限
    4. 本部门及以下数据权限
    5. 仅本人数据权限`,
)

// ============================================
// 领域服务定义 (Domain Services)
// ============================================

// 用户认证服务
export const userAuthService = d.service(
    'UserAuthService',
    d.note`用户认证服务
    1. 验证用户登录凭证
    2. 生成访问令牌
    3. 刷新令牌
    4. 支持多种认证策略(密码/SMS/邮箱/社交/微信)`,
)

// 密码加密服务
export const passwordEncryptionService = d.service(
    'PasswordEncryptionService',
    d.note`密码加密服务
    1. 使用BCrypt算法加密密码
    2. 验证密码匹配`,
)

// 验证码服务
export const captchaService = d.service(
    'CaptchaService',
    d.note`验证码服务
    1. 生成图形验证码
    2. 验证验证码有效性
    3. 发送短信验证码
    4. 发送邮箱验证码`,
)

// 用户数据权限服务
export const userDataPermissionService = d.service(
    'UserDataPermissionService',
    d.note`用户数据权限服务
    1. 根据用户角色过滤数据
    2. 根据部门过滤数据
    3. 支持自定义权限范围`,
)

// 用户导入导出服务
export const userImportExportService = d.service(
    'UserImportExportService',
    d.note`用户导入导出服务
    1. Excel批量导入用户
    2. 导出用户列表到Excel
    3. 数据验证和错误提示`,
)

// ============================================
// 外部系统定义 (External Systems)
// ============================================

// 短信系统
export const smsSystem = d.system('SmsSystem', '短信发送系统')

// 邮件系统
export const emailSystem = d.system('EmailSystem', '邮件发送系统')

// 社交登录系统
export const socialSystem = d.system('SocialSystem', '社交登录系统(JustAuth)')

// 微信小程序系统
export const xcxSystem = d.system('XcxSystem', '微信小程序系统')

// 日志系统
export const logSystem = d.system('LogSystem', '日志记录系统')

// Redis缓存系统
export const redisSystem = d.system('RedisSystem', 'Redis缓存系统')

// ============================================
// 读模型定义 (Read Models)
// ============================================

// 用户详情读模型
export const userDetailReadModel = d.readModel(
    'UserDetailReadModel',
    [
        userValues.userId,
        userValues.userName,
        userValues.nickName,
        userValues.email,
        userValues.phonenumber,
        userValues.sex,
        userValues.avatar,
        userValues.status,
        userValues.deptId,
        'deptName',
        'roles',
        'posts',
        userValues.createTime,
    ],
    d.note`用户详情读模型
    包含用户基本信息以及关联的角色、部门、岗位信息`,
)

// 用户列表读模型
export const userListReadModel = d.readModel(
    'UserListReadModel',
    [
        userValues.userId,
        userValues.userName,
        userValues.nickName,
        userValues.deptId,
        'deptName',
        userValues.phonenumber,
        userValues.status,
        userValues.createTime,
    ],
    d.note`用户列表读模型
    用于用户列表查询和分页展示`,
)

// 登录用户信息读模型
export const loginUserReadModel = d.readModel(
    'LoginUserReadModel',
    [
        userValues.userId,
        userValues.userName,
        userValues.nickName,
        userValues.email,
        userValues.phonenumber,
        userValues.sex,
        userValues.avatar,
        'roles',
        'permissions',
        'dept',
        userValues.tenantId,
    ],
    d.note`登录用户信息读模型
    用于存储当前登录用户的完整信息，包括权限列表`,
)

// ============================================
// 参与者定义 (Actors)
// ============================================

// 系统管理员
export const adminActor = d.actor('系统管理员')

// 普通用户
export const userActor = d.actor('用户')

// 访客
export const guestActor = d.actor('访客')

// 系统定时任务
export const systemScheduler = d.actor('系统定时任务')

// ============================================
// 工作流定义 (Workflows)
// ============================================

// 1. 用户注册工作流
d.startWorkflow('用户注册')
guestActor
    .command(registerUserCmd)
    .agg(userAggregate)
    .event(userCreatedEvent)
    .system(emailSystem)

// 2. 用户登录工作流 (密码方式)
d.startWorkflow('用户登录(密码)')
userActor.command(loginUserCmd).agg(userAggregate).event(userLoggedInEvent).system(logSystem)

// 3. 用户登录失败工作流
d.startWorkflow('用户登录失败')
userActor
    .command(loginUserCmd)
    .agg(userAggregate)
    .event(loginFailedEvent)
    .policy(accountLockPolicy)
    .command(updateUserStatusCmd)
    .agg(userAggregate)
    .event(userLockedEvent)

// 4. 用户登出工作流
d.startWorkflow('用户登出')
userActor.command(logoutUserCmd).agg(userAggregate).event(userLoggedOutEvent).system(logSystem)

// 5. 更新用户信息工作流
d.startWorkflow('更新用户信息')
adminActor
    .command(updateUserCmd)
    .agg(userAggregate)
    .event(userUpdatedEvent)
    .system(logSystem)

// 6. 重置用户密码工作流
d.startWorkflow('重置用户密码')
adminActor
    .command(resetUserPasswordCmd)
    .agg(userAggregate)
    // .service(passwordEncryptionService)
    .event(userPasswordResetEvent)
    .system(emailSystem)

// 7. 用户修改密码工作流
d.startWorkflow('用户修改密码')
userActor
    .command(changeUserPasswordCmd)
    .agg(userAggregate)
    // .service(passwordEncryptionService)
    .event(userPasswordChangedEvent)

// 8. 分配用户角色工作流
d.startWorkflow('分配用户角色')
adminActor.command(assignUserRolesCmd).agg(userAggregate).event(userRoleAssignedEvent)

// 9. 删除用户工作流
d.startWorkflow('删除用户')
adminActor.command(deleteUserCmd).agg(userAggregate).event(userDeletedEvent).system(logSystem)

// 10. 批量删除用户工作流
d.startWorkflow('批量删除用户')
adminActor.command(deleteUsersCmd).agg(userAggregate).event(userDeletedEvent).system(logSystem)

// 11. 用户状态变更工作流
d.startWorkflow('用户状态变更')
adminActor.command(updateUserStatusCmd).agg(userAggregate).event(userStatusUpdatedEvent)

// 12. SMS登录工作流
d.startWorkflow('用户登录(SMS)')
userActor.command(smsLoginCmd)
    // .service(captchaService)
    .agg(userAggregate)
    .event(userLoggedInEvent)

// 13. 邮箱登录工作流
d.startWorkflow('用户登录(邮箱)')
userActor.command(emailLoginCmd)
    // .service(captchaService)
    .agg(userAggregate)
    .event(userLoggedInEvent)

// 14. 社交登录工作流
d.startWorkflow('用户登录(社交)')
userActor.command(socialLoginCmd)
    // .service(socialSystem)
    .agg(userAggregate)
    .event(userLoggedInEvent)

// 15. 微信小程序登录工作流
d.startWorkflow('用户登录(微信小程序)')
userActor.command(xcxLoginCmd)
    // .service(xcxSystem)
    .agg(userAggregate)
    .event(userLoggedInEvent)

// 16. 账号自动解锁工作流
d.startWorkflow('账号自动解锁')
systemScheduler.command(updateUserStatusCmd)
    .agg(userAggregate)
    .event(userUnlockedEvent)

// 17. 用户导入工作流
d.startWorkflow('批量导入用户')
adminActor.command(importUsersCmd)
    // .service(userImportExportService)
    .agg(userAggregate)
    .event(userCreatedEvent)

// ============================================
// 用户故事定义 (User Stories)
// ============================================

// 用户注册与登录故事
d.defineUserStory('用户注册与登录', [
    '用户注册',
    '用户登录(密码)',
    '用户登录(SMS)',
    '用户登录(邮箱)',
    '用户登录(社交)',
    '用户登录(微信小程序)',
    '用户登出',
])

// 用户信息管理故事
d.defineUserStory('用户信息管理', [
    '更新用户信息',
    '用户修改密码',
    '用户状态变更',
])

// 用户权限管理故事
d.defineUserStory('用户权限管理', ['分配用户角色', '分配用户岗位'])

// 用户批量操作故事
d.defineUserStory('用户批量操作', ['批量删除用户', '批量导入用户'])

// 用户密码管理故事
d.defineUserStory('用户密码管理', ['重置用户密码', '用户修改密码'])

// ============================================
// 聚合根事件关联
// ============================================

// 用户聚合根的事件
userAggregate.event(userCreatedEvent)
userAggregate.event(userLoggedInEvent)
userAggregate.event(userLoggedOutEvent)
userAggregate.event(userUpdatedEvent)
userAggregate.event(userStatusUpdatedEvent)
userAggregate.event(userPasswordResetEvent)
userAggregate.event(userPasswordChangedEvent)
userAggregate.event(userDeletedEvent)
userAggregate.event(userLockedEvent)
userAggregate.event(userUnlockedEvent)

// 导出领域模型
export default d