DROP TABLE IF EXISTS "sys_dict_type" CASCADE;

CREATE TABLE "sys_dict_type" (
    "dict_name" TEXT NOT NULL,
    "dict_type" TEXT NOT NULL,
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

CREATE UNIQUE INDEX "idx_sys_dict_type_tenant_type" ON "sys_dict_type" ("dict_type");

COMMENT ON TABLE "sys_dict_type" IS '字典类型表';
COMMENT ON COLUMN "sys_dict_type"."dict_name" IS '字典名称';
COMMENT ON COLUMN "sys_dict_type"."dict_type" IS '字典类型';
COMMENT ON COLUMN "sys_dict_type"."remark" IS '备注';
COMMENT ON COLUMN "sys_dict_type"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_dict_type"."id" IS '主键';
COMMENT ON COLUMN "sys_dict_type"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_dict_type"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_dict_type"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_dict_type"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_dict_type"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_dict_type"."tenant_id" IS '租户编号';

