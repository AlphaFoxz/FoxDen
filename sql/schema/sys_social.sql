DROP TABLE IF EXISTS "sys_social" CASCADE;

CREATE TABLE "sys_social" (
    "user_id" BIGINT NOT NULL,
    "auth_id" TEXT DEFAULT NULL,
    "source" TEXT NOT NULL,
    "open_id" TEXT NOT NULL,
    "user_name" TEXT DEFAULT NULL,
    "nick_name" TEXT DEFAULT NULL,
    "email" TEXT DEFAULT NULL,
    "avatar" TEXT DEFAULT NULL,
    "access_token" TEXT DEFAULT NULL,
    "expire_in" BIGINT DEFAULT NULL,
    "refresh_token" TEXT DEFAULT NULL,
    "access_code" TEXT DEFAULT NULL,
    "union_id" TEXT DEFAULT NULL,
    "scope" TEXT DEFAULT NULL,
    "token_type" TEXT DEFAULT NULL,
    "id_token" TEXT DEFAULT NULL,
    "mac_algorithm" TEXT DEFAULT NULL,
    "mac_key" TEXT DEFAULT NULL,
    "code" TEXT DEFAULT NULL,
    "oauth_token" TEXT DEFAULT NULL,
    "oauth_token_secret" TEXT DEFAULT NULL,
    "del_flag" BOOLEAN NOT NULL,
    "id" BIGINT NOT NULL,
    "create_dept" BIGINT NOT NULL,
    "create_by" BIGINT NOT NULL,
    "create_time" TIMESTAMPTZ(0) NOT NULL,
    "update_by" BIGINT NOT NULL,
    "update_time" TIMESTAMPTZ(0) NOT NULL,
    "tenant_id" TEXT NOT NULL,
    PRIMARY KEY ("id")
);

COMMENT ON TABLE "sys_social" IS '社会化关系表';
COMMENT ON COLUMN "sys_social"."user_id" IS '用户ID';
COMMENT ON COLUMN "sys_social"."auth_id" IS '平台+平台唯一id';
COMMENT ON COLUMN "sys_social"."source" IS '用户来源';
COMMENT ON COLUMN "sys_social"."open_id" IS '平台编号唯一id';
COMMENT ON COLUMN "sys_social"."user_name" IS '登录账号';
COMMENT ON COLUMN "sys_social"."nick_name" IS '用户昵称';
COMMENT ON COLUMN "sys_social"."email" IS '用户邮箱';
COMMENT ON COLUMN "sys_social"."avatar" IS '头像地址';
COMMENT ON COLUMN "sys_social"."access_token" IS '用户的授权令牌';
COMMENT ON COLUMN "sys_social"."expire_in" IS '用户的授权令牌的有效期';
COMMENT ON COLUMN "sys_social"."refresh_token" IS '刷新令牌';
COMMENT ON COLUMN "sys_social"."access_code" IS '平台的授权信息';
COMMENT ON COLUMN "sys_social"."union_id" IS '用户的 unionid';
COMMENT ON COLUMN "sys_social"."scope" IS '授予的权限';
COMMENT ON COLUMN "sys_social"."token_type" IS '个别平台的授权信息';
COMMENT ON COLUMN "sys_social"."id_token" IS 'id token';
COMMENT ON COLUMN "sys_social"."mac_algorithm" IS '小米平台用户的附带属性';
COMMENT ON COLUMN "sys_social"."mac_key" IS '小米平台用户的附带属性';
COMMENT ON COLUMN "sys_social"."code" IS '用户的授权code';
COMMENT ON COLUMN "sys_social"."oauth_token" IS 'Twitter平台用户的附带属性';
COMMENT ON COLUMN "sys_social"."oauth_token_secret" IS 'Twitter平台用户的附带属性';
COMMENT ON COLUMN "sys_social"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_social"."id" IS '主键';
COMMENT ON COLUMN "sys_social"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_social"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_social"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_social"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_social"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_social"."tenant_id" IS '租户编号';

