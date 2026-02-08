DROP TABLE IF EXISTS "sys_config" CASCADE;

CREATE TABLE "sys_config" (
    "config_name" TEXT NOT NULL,
    "config_key" TEXT NOT NULL,
    "config_value" TEXT DEFAULT NULL,
    "config_type" TEXT DEFAULT NULL,
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

COMMENT ON TABLE "sys_config" IS '参数配置表';
COMMENT ON COLUMN "sys_config"."config_name" IS '参数名称';
COMMENT ON COLUMN "sys_config"."config_key" IS '参数键名';
COMMENT ON COLUMN "sys_config"."config_value" IS '参数键值';
COMMENT ON COLUMN "sys_config"."config_type" IS '系统内置（Y是 N否）';
COMMENT ON COLUMN "sys_config"."remark" IS '备注';
COMMENT ON COLUMN "sys_config"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_config"."id" IS '主键';
COMMENT ON COLUMN "sys_config"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_config"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_config"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_config"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_config"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_config"."tenant_id" IS '租户编号';

