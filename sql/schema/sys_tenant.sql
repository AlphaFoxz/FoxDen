DROP TABLE IF EXISTS "sys_tenant" CASCADE;

CREATE TABLE "sys_tenant" (
    "contact_user_name" TEXT DEFAULT NULL,
    "contact_phone" TEXT DEFAULT NULL,
    "company_name" TEXT NOT NULL,
    "license_number" TEXT DEFAULT NULL,
    "address" TEXT DEFAULT NULL,
    "intro" TEXT DEFAULT NULL,
    "domain" TEXT DEFAULT NULL,
    "remark" TEXT DEFAULT NULL,
    "package_id" BIGINT DEFAULT NULL,
    "expire_time" TIMESTAMP(6) DEFAULT NULL,
    "account_count" INT DEFAULT NULL,
    "status" TEXT NOT NULL,
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

COMMENT ON TABLE "sys_tenant" IS '租户表';
COMMENT ON COLUMN "sys_tenant"."contact_user_name" IS '联系人';
COMMENT ON COLUMN "sys_tenant"."contact_phone" IS '联系电话';
COMMENT ON COLUMN "sys_tenant"."company_name" IS '企业名称';
COMMENT ON COLUMN "sys_tenant"."license_number" IS '统一社会信用代码';
COMMENT ON COLUMN "sys_tenant"."address" IS '地址';
COMMENT ON COLUMN "sys_tenant"."intro" IS '企业简介';
COMMENT ON COLUMN "sys_tenant"."domain" IS '域名';
COMMENT ON COLUMN "sys_tenant"."remark" IS '备注';
COMMENT ON COLUMN "sys_tenant"."package_id" IS '租户套餐编号';
COMMENT ON COLUMN "sys_tenant"."expire_time" IS '过期时间';
COMMENT ON COLUMN "sys_tenant"."account_count" IS '用户数量（-1不限制）';
COMMENT ON COLUMN "sys_tenant"."status" IS '租户状态（0正常 1停用）';
COMMENT ON COLUMN "sys_tenant"."del_flag" IS '删除标识';
COMMENT ON COLUMN "sys_tenant"."id" IS '主键';
COMMENT ON COLUMN "sys_tenant"."create_dept" IS '创建部门';
COMMENT ON COLUMN "sys_tenant"."create_by" IS '创建者';
COMMENT ON COLUMN "sys_tenant"."create_time" IS '创建时间';
COMMENT ON COLUMN "sys_tenant"."update_by" IS '更新者';
COMMENT ON COLUMN "sys_tenant"."update_time" IS '更新时间';
COMMENT ON COLUMN "sys_tenant"."tenant_id" IS '租户编号';

