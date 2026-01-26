DROP TABLE IF EXISTS "sys_oss_config" CASCADE;

CREATE TABLE "sys_oss_config" (
    "config_key" TEXT NOT NULL,
    "access_key" TEXT NOT NULL,
    "secret_key" TEXT NOT NULL,
    "bucket_name" TEXT NOT NULL,
    "prefix" TEXT DEFAULT NULL,
    "endpoint" TEXT NOT NULL,
    "domain" TEXT DEFAULT NULL,
    "https" BOOLEAN DEFAULT NULL,
    "region" TEXT DEFAULT NULL,
    "access_policy" TEXT NOT NULL,
    "status" TEXT NOT NULL,
    "ext1" TEXT DEFAULT NULL,
    "remark" TEXT DEFAULT NULL,
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

COMMENT ON TABLE "sys_oss_config" IS '对象存储配置表';
COMMENT ON COLUMN "sys_oss_config"."config_key" IS '配置key';
COMMENT ON COLUMN "sys_oss_config"."access_key" IS 'accessKey';
COMMENT ON COLUMN "sys_oss_config"."secret_key" IS '秘钥';
COMMENT ON COLUMN "sys_oss_config"."bucket_name" IS '桶名称';
COMMENT ON COLUMN "sys_oss_config"."prefix" IS '前缀';
COMMENT ON COLUMN "sys_oss_config"."endpoint" IS '访问站点';
COMMENT ON COLUMN "sys_oss_config"."domain" IS '自定义域名';
COMMENT ON COLUMN "sys_oss_config"."https" IS '是否https';
COMMENT ON COLUMN "sys_oss_config"."region" IS '域';
COMMENT ON COLUMN "sys_oss_config"."access_policy" IS '桶权限类型(0=private 1=public 2=custom)';
COMMENT ON COLUMN "sys_oss_config"."status" IS '是否默认（0=是,1=否）';
COMMENT ON COLUMN "sys_oss_config"."ext1" IS '扩展字段';
COMMENT ON COLUMN "sys_oss_config"."remark" IS '备注';
COMMENT ON COLUMN "sys_oss_config"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_oss_config"."id" IS '主键';
COMMENT ON COLUMN "sys_oss_config"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_oss_config"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_oss_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_oss_config"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_oss_config"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_oss_config"."tenant_id" IS '租户编号';

