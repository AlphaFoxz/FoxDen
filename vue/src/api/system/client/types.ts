export interface ClientVO {
  /**
   * Id
   */
  id: string | number;

  /**
   * 客户端id
   */
  clientId: string;

  /**
   * 客户端key
   */
  clientKey: string;

  /**
   * 客户端秘钥
   */
  clientSecret: string;

  /**
   * 授权类型
   */
  grantTypeList: string[];

  /**
   * 设备类型
   */
  deviceType: string;

  /**
   * Token活跃超时时间
   */
  activeTimeout: number;

  /**
   * Token固定超时
   */
  timeout: number;

  /**
   * 状态（0正常 1停用）
   */
  status: string;
}

export interface ClientForm extends BaseEntity {
  /**
   * Id
   */
  id?: string | number;

  /**
   * 客户端id
   */
  clientId?: string | number;

  /**
   * 客户端key
   */
  clientKey?: string;

  /**
   * 客户端秘钥
   */
  clientSecret?: string;

  /**
   * 授权类型
   */
  grantTypeList?: string[];

  /**
   * 设备类型
   */
  deviceType?: string;

  /**
   * Token活跃超时时间
   */
  activeTimeout?: number;

  /**
   * Token固定超时
   */
  timeout?: number;

  /**
   * 状态（0正常 1停用）
   */
  status?: string;
}

export interface ClientQuery extends PageQuery {
  /**
   * 客户端id
   */
  clientId?: string | number;

  /**
   * 客户端key
   */
  clientKey?: string;

  /**
   * 客户端秘钥
   */
  clientSecret?: string;

  /**
   * 授权类型
   */
  grantType?: string;

  /**
   * 设备类型
   */
  deviceType?: string;

  /**
   * Token活跃超时时间
   */
  activeTimeout?: number;

  /**
   * Token固定超时
   */
  timeout?: number;

  /**
   * 状态（0正常 1停用）
   */
  status?: string;
}
