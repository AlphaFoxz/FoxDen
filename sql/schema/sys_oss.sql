DROP TABLE IF EXISTS "sys_oss" CASCADE;

CREATE TABLE "sys_oss" (
    "file_name" TEXT NOT NULL,
    "original_name" TEXT DEFAULT NULL,
    "file_suffix" TEXT DEFAULT NULL,
    "url" TEXT NOT NULL,
    "ext1" TEXT DEFAULT NULL,
    "service" TEXT DEFAULT NULL,
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

COMMENT ON TABLE "sys_oss" IS 'OSS对象存储表';
COMMENT ON COLUMN "sys_oss"."file_name" IS '文件名';
COMMENT ON COLUMN "sys_oss"."original_name" IS '原名';
COMMENT ON COLUMN "sys_oss"."file_suffix" IS '文件后缀名';
COMMENT ON COLUMN "sys_oss"."url" IS 'URL地址';
COMMENT ON COLUMN "sys_oss"."ext1" IS '扩展字段';
COMMENT ON COLUMN "sys_oss"."service" IS '服务商';
COMMENT ON COLUMN "sys_oss"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_oss"."id" IS '主键';
COMMENT ON COLUMN "sys_oss"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_oss"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_oss"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_oss"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_oss"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_oss"."tenant_id" IS '租户编号';

