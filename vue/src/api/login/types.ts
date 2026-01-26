/**
 * 注册
 */
export type RegisterForm = {
  tenantId: string;
  username: string;
  password: string;
  confirmPassword?: string;
  code?: string;
  uuid?: string;
  userType?: string;
};

/**
 * 登录请求
 */
export type LoginData = {
  tenantId?: string;
  username?: string;
  password?: string;
  rememberMe?: boolean;
  socialCode?: string;
  socialState?: string;
  source?: string;
  code?: string;
  uuid?: string;
  clientId: string;
  grantType: string;
};

/**
 * 登录响应
 */
export type LoginResult = {
  access_token: string;
};

/**
 * 验证码返回
 */
export type VerifyCodeResult = {
  captchaEnabled: boolean;
  uuid?: string;
  img?: string;
};

/**
 * 租户
 */
export type TenantVO = {
  companyName: string;
  domain: any;
  tenantId: string;
};

export type TenantInfo = {
  tenantEnabled: boolean;
  voList: TenantVO[];
};
