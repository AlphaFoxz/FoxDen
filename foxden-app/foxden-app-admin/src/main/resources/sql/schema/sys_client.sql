DROP TABLE IF EXISTS "sys_client" CASCADE;

CREATE TABLE "sys_client" (
    "client_id" TEXT NOT NULL,
    "client_key" TEXT NOT NULL,
    "client_secret" TEXT NOT NULL,
    "grant_type" TEXT NOT NULL,
    "device_type" TEXT DEFAULT NULL,
    "active_timeout" INT DEFAULT NULL,
    "timeout" INT DEFAULT NULL,
    "status" TEXT NOT NULL,
    "del_flag" BOOLEAN NOT NULL,
    "id" BIGINT NOT NULL,
    "create_dept" BIGINT NOT NULL,
    "create_by" BIGINT NOT NULL,
    "create_time" TIMESTAMPTZ(0) NOT NULL,
    "update_by" BIGINT NOT NULL,
    "update_time" TIMESTAMPTZ(0) NOT NULL,
    PRIMARY KEY ("id")
);

COMMENT ON TABLE "sys_client" IS '系统授权表';
COMMENT ON COLUMN "sys_client"."client_id" IS '客户端id';
COMMENT ON COLUMN "sys_client"."client_key" IS '客户端key';
COMMENT ON COLUMN "sys_client"."client_secret" IS '客户端秘钥';
COMMENT ON COLUMN "sys_client"."grant_type" IS '授权类型';
COMMENT ON COLUMN "sys_client"."device_type" IS '设备类型';
COMMENT ON COLUMN "sys_client"."active_timeout" IS 'token活跃超时时间';
COMMENT ON COLUMN "sys_client"."timeout" IS 'token固定超时';
COMMENT ON COLUMN "sys_client"."status" IS '状态（0正常 1停用）';
COMMENT ON COLUMN "sys_client"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_client"."id" IS '主键';
COMMENT ON COLUMN "sys_client"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_client"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_client"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_client"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_client"."update_time" IS '更新时间';

